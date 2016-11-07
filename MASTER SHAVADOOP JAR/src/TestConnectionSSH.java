import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


public class TestConnectionSSH extends Thread{
	
	private String machine;
	private int timeout;
	private ArrayBlockingQueue<String> standard_output = new ArrayBlockingQueue<String>(1000);
	private ArrayBlockingQueue<String> error_output = new ArrayBlockingQueue<String>(1000);
	private boolean connectionOK = false;
	
	public String getMachine() {
		return machine;
	}

	public void setConnectionOK(boolean connectionOK) {
		this.connectionOK = connectionOK;
	}

	public boolean isConnectionOK() {
		return connectionOK;
	}

	public TestConnectionSSH(String machine, int timeout){
		this.machine=machine;
		this.timeout = timeout;
	}
	
	public void affiche(String texte){
		System.out.println("[TestConnectionSSH "+machine+"] "+texte);
	}
	
	public void run(){
	 try {
         String[] commande = {"ssh","-o StrictHostKeyChecking=no",machine, "echo OK"};
            ProcessBuilder pb = new ProcessBuilder(commande);
            Process p = pb.start();
            LecteurFlux fluxSortie = new LecteurFlux(p.getInputStream(), standard_output);
            LecteurFlux fluxErreur = new LecteurFlux(p.getErrorStream(), error_output);

            new Thread(fluxSortie).start();
            new Thread(fluxErreur).start();

            String s = standard_output.poll(timeout, TimeUnit.SECONDS);
            while(s!=null && !s.equals("ENDOFTHREAD")){
            	affiche(s);
            	if(s.contains("OK")){
            		connectionOK = true;
            	}
            	s = standard_output.poll(timeout, TimeUnit.SECONDS);
            }
            
            s = null;
            s = error_output.poll(timeout, TimeUnit.SECONDS);
            while(s!=null && !s.equals("ENDOFTHREAD")){
            	affiche(s);
            	s = error_output.poll(timeout, TimeUnit.SECONDS);
            }
         
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
