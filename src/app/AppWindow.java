package app;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;

import java.awt.EventQueue;
import java.awt.List;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.event.ChangeEvent;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

public class AppWindow {
	private static int finalTotal = 0;	
	private static ArrayList<Path> filesForCopy = new ArrayList<Path>(); 
	private File[] GetRootOfAllDrives(JTextArea statuspanel) {
		File[] paths;		
		paths = File.listRoots();
		for(File path:paths)
		{
			statuspanel.append("Found disk: "+path+"\n");
		}
		
		return paths;
	}
	
	public static class Finder
    extends SimpleFileVisitor<Path> {
		
		private final PathMatcher matcher;
		private JTextArea outputPanel;
	    private int numMatches = 0;
	    private String pattern;
	    Finder(String pattern, JTextArea statuspanel) {
	        matcher = FileSystems.getDefault()
	                .getPathMatcher("glob:" + pattern);
	        outputPanel = statuspanel;
	        this.pattern = pattern;
	    }
	    
	    void find(Path file) {
	        Path name = file.getFileName();
	        if (name != null && matcher.matches(name)) {
	            numMatches++;
	            outputPanel.append(file +"\n");	           
	            filesForCopy.add(file);	          
	        }
	    }
	    
	    void done() {
	        outputPanel.append("Found: "+numMatches+" " + pattern +" \n");
	        finalTotal = finalTotal + numMatches;	       
	    }
	    
	    @Override
	    public FileVisitResult visitFile(Path file,
	            BasicFileAttributes attrs) {
	        find(file);
	        return CONTINUE;
	    }
	    @Override
	    public FileVisitResult preVisitDirectory(Path dir,
	            BasicFileAttributes attrs) {
	        find(dir);
	        return CONTINUE;
	    }
	    
	    @Override
	    public FileVisitResult visitFileFailed(Path file,
	            IOException exc) {
	    		outputPanel.append("Somethins went wron... :( ");
	        return CONTINUE;
	    }
	    
	}
	private void CopyFilesToCurrentFolder(JTextArea output)
	{
		File currentDirectory = new File(new File("jpgs").getAbsolutePath());		
		output.append("Starting to copy");		
		if(currentDirectory.mkdir() || currentDirectory.isDirectory())
		{		 
			
			try {
				for(Path jpg: filesForCopy ) {	
					 InputStream in = new FileInputStream(jpg.toFile());
					 OutputStream out = new FileOutputStream(currentDirectory+"/"+jpg.toFile().getName());
					 byte[] buf = new byte[1024];
		                int len;
		                while ((len = in.read(buf)) > 0) {
		                    out.write(buf, 0, len);
		                }
		                in.close();
		                out.close();
					
				}
			    
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
		output.append("All done");
		
		
		
		
	}

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = new AppWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		 
		frame = new JFrame();
		frame.setBounds(100, 100, 756, 566);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(54, 261, 626, 243);
		frame.getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		
		JButton btnNewButton = new JButton("Get pictures from all drives");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File[] allroots = GetRootOfAllDrives(textArea);
				
				for(File path:allroots)
				{
					//File path = allroots[0];
					textArea.append("Working on drive: "+path+"\n");
					String pathToString = path.toString();
					String slash = "\\";
					String s = new StringBuilder(pathToString).append(slash).toString();
					
					Path startingDir = Paths.get(s);
					String patternToLookFor = "*.jpg";
					//String[] patternsToLookFor = {"*.jpg", "*.bmp", "*.png"}; predugo mi je trajalo kada sam radio sve
					//for(String pattern :patternsToLookFor ) {
					Finder finder= new Finder(patternToLookFor,textArea);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {						
						e.printStackTrace();
					}
					//Finder finder= new Finder(pattern,textArea);
					try {
						//textArea.append("Starting "+ path + " "+ "looking for"+ pattern);
						textArea.append("Starting "+ path + " "+ "looking for"+ " "+patternToLookFor);
						Files.walkFileTree(startingDir, finder);
					} catch (IOException e) {
						textArea.append("Something broke...");
						e.printStackTrace();
					}
					finder.done();	
					//stavljeno je da se prikažu poruke
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {						
						e.printStackTrace();
					}				
					textArea.append("Done with drive: "+path + " current total images: "+finalTotal);					
				}
				
			}
		});
		btnNewButton.setBounds(54, 43, 227, 49);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Coppy JPGs to program folder");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CopyFilesToCurrentFolder(textArea);
			}
		});
		btnNewButton_1.setBounds(54, 168, 227, 49);
		frame.getContentPane().add(btnNewButton_1);
	}	
	
}
