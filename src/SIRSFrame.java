import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;




@SuppressWarnings("serial")
class SIRSPanel extends JPanel{
	Organism organism;
	int organismSize;

	public SIRSPanel(Organism organism){
		this.organism = organism;
		this.organismSize = organism.getCells().length;
		setBackground(Color.BLACK);
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		int N = getN(organism);

		int w = getWidth()/N;
		int h = getHeight()/N;

		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				//Here the Cells of the Organism are coloured depending on their state.
				if(getOrganism().getState(i, j).equals("S")){
					g.setColor(Color.RED);
					g.fillRect(w*i, h*j, w, h);
				}else if (getOrganism().getState(i, j).equals("I")){
					g.setColor(Color.GREEN);
					g.fillRect(w*i, h*j, w, h);
				}else if(getOrganism().getState(i, j).equals("R")){
					g.setColor(Color.WHITE);
					g.fillRect(w*i, h*j, w, h);
				}else if(getOrganism().getState(i, j).equals("RR")){
					g.setColor(Color.BLUE);
					g.fillRect(w*i, h*j, w, h);
				}
			}
		}
	}

	public int getN(Organism organism){
		return organism.getCells().length;
	}

	public Organism getOrganism(){
		return organism;
	}

	public void updateP1(double p1){
		organism.setP1(p1);
		repaint();
	}

	public void updateP2(double p2){
		organism.setP2(p2);
		repaint();
	}

	public void updateP3(double p3){
		organism.setP3(p3);
		repaint();
	}
	
	public void changeSize(int n){
		organism = Organism.equalRandomOrganism(n, organism.getP1(), organism.getP2(), organism.getP3());
		repaint();
	}
	
	public void changeImmunity(double fractionImmune){
		organism = Organism.fractionImmuneOrganism(organism.getCells().length, fractionImmune, organism.getP1(), organism.getP2(), organism.getP3());
		repaint();
	}

	public void pandemicAnimate(){
		for(int i = 0; i < organism.getCells().length*organism.getCells().length; i++){
			organism.spreadDisease();
		}
		repaint();
	}
	
	public void resetRandomly(){
		organism = Organism.equalRandomOrganism(getN(organism), organism.getP1(), organism.getP2(), organism.getP3());
		repaint();
	}
}


@SuppressWarnings("serial")
public class SIRSFrame extends JFrame{

	SIRSPanel sPan;
	Organism organism;
	int N = 256;
	double p1 = 1;
	double p2 = 1;
	double p3 = 0;
	Timer pandemicTimer;

	JLabel p1Label = new JLabel("P1: ");
	JTextField p1Field = new JTextField(String.valueOf(p1), 10);
	JLabel p2Label = new JLabel("P2: ");
	JTextField p2Field = new JTextField(String.valueOf(p2), 10);
	JLabel p3Label = new JLabel("P3: ");
	JTextField p3Field = new JTextField(String.valueOf(p3), 10);

	JLabel sizeLabel = new JLabel("Size: ");
	JTextField sizeField = new JTextField(String.valueOf(N), 5);
	JLabel immuneLabel = new JLabel("Fraction immune: ");
	JTextField immuneField = new JTextField("0.0", 5);
	JButton goButton = new JButton("Go");
	JButton resetRandButton = new JButton("Reset (random)");

	public SIRSFrame(){

		organism = Organism.equalRandomOrganism(N, p1, p2, p3);
		sPan = new SIRSPanel(organism);
		sPan.setPreferredSize(new Dimension(512, 512));

		JPanel probabilityPanel = new JPanel();
		probabilityPanel.add(p1Label);
		probabilityPanel.add(p1Field);
		probabilityPanel.add(p2Label);
		probabilityPanel.add(p2Field);
		probabilityPanel.add(p3Label);
		probabilityPanel.add(p3Field);

		JPanel controlPanel = new JPanel();
		controlPanel.add(sizeLabel);
		controlPanel.add(sizeField);
		controlPanel.add(immuneLabel);
		controlPanel.add(immuneField);
		controlPanel.add(goButton);
		controlPanel.add(resetRandButton);

		getContentPane().add(sPan, BorderLayout.CENTER);
		getContentPane().add(probabilityPanel, BorderLayout.NORTH);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		pack();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				System.exit(0);
			}
		});

		setTitle("SIRS Model");
		setLocation(100, 20);
		setVisible(true);
		setBackground(Color.LIGHT_GRAY);

		updateP1();
		updateP2();
		updateP3();
		changeSize();
		changeImmunity();
		go();
		resetRandom();
	}

	public void updateP1(){
		
		p1Field.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        p1Field.setText("");
		    }
		});
		
		p1Field.addActionListener((e)->{
			double p1 = Double.parseDouble(p1Field.getText());
			sPan.updateP1(p1);
		});
	}

	public void updateP2(){
		
		p2Field.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        p2Field.setText("");
		    }
		});
		
		p2Field.addActionListener((e)->{
			double p2 = Double.parseDouble(p2Field.getText());
			sPan.updateP2(p2);
		});
	}

	public void updateP3(){
		
		p3Field.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        p3Field.setText("");
		    }
		});
		
		p3Field.addActionListener((e)->{
			double p3 = Double.parseDouble(p3Field.getText());
			sPan.updateP3(p3);
		});
	}
	
	public void changeSize(){
		
		sizeField.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        sizeField.setText("");
		    }
		});
		
		sizeField.addActionListener((e)->{
			int n = Integer.parseInt(sizeField.getText());
			sPan.changeSize(n);
		});
	}
	
	public void changeImmunity(){
		
		immuneField.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        immuneField.setText("");
		    }
		});
		
		immuneField.addActionListener((e)->{
			double fractionImmune = Double.parseDouble(immuneField.getText());
			sPan.changeImmunity(fractionImmune);
		});
	}

	public void go(){
		pandemicTimer = new Timer(0, (e)->{
			sPan.pandemicAnimate();
		});

		goButton.addActionListener((e)->{
			
			if(!pandemicTimer.isRunning()){
				pandemicTimer.start();
				goButton.setText("Stop");
			}else{
				pandemicTimer.stop();
				goButton.setText("Go");
			}
		});
	}
	
	public void resetRandom(){
		
		resetRandButton.addActionListener((e)->{
			
			if(pandemicTimer.isRunning()) pandemicTimer.stop();
			if(goButton.getText().equalsIgnoreCase("stop")) goButton.setText("Go");
			sPan.resetRandomly();
		});
	}
	
}





