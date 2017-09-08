/*
Copyright (c) Microsoft Corporation
All rights reserved. 
MIT License
 
Permission is hereby granted, free of charge, to any person obtaining a copy of this 
software and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, merge, 
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons 
to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Software.
THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.microsoft.AzureIntelligentServicesExample;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.microsoft.AzureIntelligentServicesExample.SpeechAPI.Language;
import com.microsoft.AzureIntelligentServicesExample.SpeechAPI.OutputFormat;
import com.microsoft.AzureIntelligentServicesExample.SpeechAPI.RecognitionMode;

public class ExtendedExample extends JPanel implements ActionListener {
	private final JButton openButton, micButton;
	private final JTextArea log;
	private final JFileChooser fc;
	private final JTextField keyField;
	
	private final JComboBox<RecognitionMode> modeBox;
	private final JComboBox<Language> languageBox;
	private final JComboBox<OutputFormat> formatBox;
	
	
	private volatile boolean bootstrapped = false;
	private SpeechClientREST speechClient;

	public ExtendedExample() {
		
		super(new BorderLayout());
		
		keyField = new JTextField(32);
		modeBox = new JComboBox<>(SpeechAPI.RecognitionMode.values());
		languageBox = new JComboBox<>(SpeechAPI.Language.values());
		formatBox = new JComboBox<>(SpeechAPI.OutputFormat.values());

		log = new JTextArea(20, 40);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		log.setLineWrap(true);
		JScrollPane logScrollPane = new JScrollPane(log);

		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("WAV audio files", "wav"));

		
		openButton = new JButton("Transcribe File", 
				UIManager.getIcon("FileView.directoryIcon"));
		openButton.addActionListener(this);
		
		// Create the save button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).
		micButton = new JButton("Use Microphone",
				UIManager.getIcon("Tree.expandedIcon"));
		micButton.addActionListener(this);

		// For layout purposes, put the buttons in a separate panel
		JPanel midPanel = new JPanel(); // use FlowLayout
		midPanel.add(openButton);
		midPanel.add(micButton);
		
		
		JPanel knobs = new JPanel(new GridLayout(0,2));
		knobs.add(new JLabel("Recognition mode:"));
		knobs.add(modeBox);
		knobs.add(new JLabel("Recognition language:"));
		knobs.add(languageBox);
		knobs.add(new JLabel("Output format:"));
		knobs.add(formatBox);
		
		
		midPanel.add(knobs);
		
		openButton.setEnabled(false);
		micButton.setEnabled(false);

		JPanel keyPanel = new JPanel(); // use FlowLayout
		keyPanel.add(new JLabel("Please, enter your subscription key:"));
		keyPanel.add(keyField);
		keyField.setEditable(true);
		
		keyField.addActionListener((ActionEvent e) -> {
			if (bootstrapped) 
				return;
			
        	String text = keyField.getText();
            if (text != null && text.length() == 32) {
            		log.append(String.format("Using subscription key '%s' to  generate an access token...", text));
                	CompletableFuture
                		.supplyAsync(() -> {return new RenewableAuthentication(text);})
                		.thenAccept(this::bootstrap);
            } else if (text != null) {
            	log.append(String.format("Subscription key is too %s.\n", text.length() < 32 ? "short" : "long"));
            }
        });
		
		add(keyPanel, BorderLayout.NORTH);
		add(midPanel, BorderLayout.CENTER);
		add(logScrollPane, BorderLayout.SOUTH);
		
		languageBox.setSelectedItem(SpeechAPI.Language.en_US);
	}
	
	private synchronized void bootstrap(Authentication auth) {
		if (bootstrapped) 
			return;
		
		if (auth.getToken() != null) {
			bootstrapped = true;
			
    		speechClient = new SpeechClientREST(auth);
        	openButton.setEnabled(true);
        	micButton.setEnabled(true);
        	keyField.setEnabled(false);
        	log.append("done!\n");
    	} else {
    		log.append("ups...something went wrong, please try again.\n");
    	}
	}

	public void actionPerformed(ActionEvent e) {
		
		speechClient.setMode(modeBox.getItemAt(modeBox.getSelectedIndex()));
		speechClient.setLanguage(languageBox.getItemAt(languageBox.getSelectedIndex()));
		speechClient.setFormat(formatBox.getItemAt(formatBox.getSelectedIndex()));
		
		if (e.getSource() == openButton) {
			int returnVal = fc.showOpenDialog(ExtendedExample.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				// This is where a real application would open the file.
				log.append("Processing: " + file.getName() + "... Please, stand by!\n");
				
				CompletableFuture.runAsync(() -> {
					try {
						String result = speechClient.process(file.toPath());
						log.append(String.format("Speech recognition results:\n%s\n", result));
					} catch (IOException error) {
						log.append(String.format("Ups...something went wrong (%s).\n", error.getMessage()));
					}
				});
			} 

		} else if (e.getSource() == micButton) {
			log.append("Recording microphone input (15 seconds) ... ");
			CompletableFuture.runAsync(this::processMicrophoneInput);
		}
	}

	private synchronized void processMicrophoneInput() {
		micButton.setEnabled(false);
		AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
		try (
				TargetDataLine microphone = AudioSystem.getTargetDataLine(format);
				PipedOutputStream source = new PipedOutputStream();
			) {

			microphone.open(format);
			microphone.start();

			CompletableFuture.runAsync(() -> {
				try (PipedInputStream sink = new PipedInputStream()) {
					source.connect(sink);
					String result = speechClient.process(sink);
					log.append(String.format("Speech recognition results:\n%s\n", result));
				} catch (IOException error) {
					log.append(String.format(
							"Ups...something went wrong (%s).\n",
							error.getMessage()));
				} 
			});

			byte[] buffer = new byte[1024];
			// 15 seconds of audio in bytes =
			// 16000 (samples per second) * 2 (bytes per sample) * 15 (seconds)
			for (int i = 0; i < 16000 * 2 * 3 / buffer.length; i++) {
				int count = microphone.read(buffer, 0, buffer.length);
				source.write(buffer, 0, count);
			}

		} catch (Exception error) {
			log.append(String.format("Microphone is not working (%s).\n", error.getMessage()));
		} finally {
			micButton.setEnabled(true);
		}

	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Extended Speech Services Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new ExtendedExample());
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
}