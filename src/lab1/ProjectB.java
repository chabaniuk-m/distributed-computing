package lab1;

import javax.swing.*;

public class ProjectB {
    /**
     * 0 - вільно
     * 1 - зайнято, працює перший потік
     * 2 - зайнято, працює другий потік
     */
    public static int semaphore = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(700, 400, 400, 300);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        JSlider slider = new JSlider(0,100,50);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBounds(50, 20, 300, 40);
        panel.add(slider);

        JButton startBtn1 = new JButton("ПУСК 1");
        JButton startBtn2 = new JButton("ПУСК 2");
        JButton stopBtn1 = new JButton("СТОП 1");
        JButton stopBtn2 = new JButton("СТОП 2");

        startBtn1.setBounds(80, 80,100, 30);
        startBtn2.setBounds(220, 80,100, 30);
        stopBtn1.setBounds(80, 130, 100, 30);
        stopBtn2.setBounds(220, 130, 100, 30);

        panel.add(startBtn1);
        panel.add(startBtn2);
        panel.add(stopBtn1);
        panel.add(stopBtn2);

        JLabel label = new JLabel();
        label.setBounds(100, 180, 200, 30);
        panel.add(label);

        Thread th1 = new Thread(new Runner1(slider));
        Thread th2 = new Thread(new Runner2(slider));

        startBtn1.addActionListener(e -> {
            if (semaphore == 0) {
                if (!th1.isInterrupted()) {
                    semaphore = 1;
                    th1.setPriority(Thread.MIN_PRIORITY);
                    th1.start();
                    label.setText("Виконується перший потік");
                }
            } else {
                label.setText("Зайнято потоком");
            }
        });
        startBtn2.addActionListener(e -> {
            if (semaphore == 0) {
                if (!th2.isInterrupted()){
                    semaphore = 2;
                    th2.setPriority(Thread.MAX_PRIORITY);
                    th2.start();
                    label.setText("Виконується другий потік");
                }
            } else {
                label.setText("Зайнято потоком");
            }
        });
        stopBtn1.addActionListener(e -> {
            if (semaphore == 1) {
                th1.interrupt();
                semaphore = 0;
                label.setText("Вільно");
            }
        });
        stopBtn2.addActionListener(e -> {
            if (semaphore == 2) {
                th2.interrupt();
                semaphore = 0;
                label.setText("Вільно");
            }
        });
    }
}

class Runner1 implements Runnable {
    private final JSlider slider;

    public Runner1(JSlider slider) {
        this.slider = slider;
    }

    @Override
    public void run() {
        slider.setValue(10);
    }
}

class Runner2 implements Runnable {
    private final JSlider slider;

    public Runner2(JSlider slider) {
        this.slider = slider;
    }

    @Override
    public void run() {
        slider.setValue(90);
    }
}
