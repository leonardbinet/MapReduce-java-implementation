import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


public class LaunchSlaveShavadoop extends Thread{
	
	private String machine;
	private String command;
	private int timeout;
	private ArrayBlockingQueue<String> standard_output = new ArrayBlockingQueue<String>(1000);
	private ArrayBlockingQueue<String> error_output = new ArrayBlockingQueue<String>(1000);
	private ArrayList<String> reponse;
	
	public String getMachine() {
		return machine;
	}


	public LaunchSlaveShavadoop(String machine, String command, int timeout){
		this.machine=machine;
		this.timeout = timeout;
		this.command = command;
		this.reponse = new ArrayList<String>();
	}
	
	public void affiche(String texte){
		System.out.println("[TestConnectionSSH "+machine+"] "+texte);
	}
	
	public void run(){
	 try {
         String[] commande = {"ssh","-o StrictHostKeyChecking=no",machine, command};
            ProcessBuilder pb = new ProcessBuilder(commande);
            Process p = pb.start();
            LecteurFlux fluxSortie = new LecteurFlux(p.getInputStream(), standard_output);
            LecteurFlux fluxErreur = new LecteurFlux(p.getErrorStream(), error_output);

            new Thread(fluxSortie).start();
            new Thread(fluxErreur).start();

            String s = standard_output.poll(timeout, TimeUnit.SECONDS);
            while(s!=null && !s.equals("ENDOFTHREAD")){
            	affiche(s);
            	this.reponse.add(s);
            	s = standard_output.poll(timeout, TimeUnit.SECONDS);
            }
            
            s = null;
            s = error_output.poll(timeout, TimeUnit.SECONDS);
            while(s!=null && !s.equals("ENDOFTHREAD")){
            	affiche(s);
            	this.reponse.add(s);
            	s = error_output.poll(timeout, TimeUnit.SECONDS);
            }
         
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	public ArrayList<String> get_response(){
		return this.reponse;
	}

}
