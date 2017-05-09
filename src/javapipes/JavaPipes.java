/*
*                        Pipe Producer-Consumer Example
*                              Joseph Polen
*
*               This program demonstrates a method of process synchronization 
*               using piped threads in Java.
*
 */
package javapipes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class JavaPipes {

    public static void main(String[] args) throws IOException {
        //Create output pipe object
        PipedOutputStream outputPipe = new PipedOutputStream();
        //Create input pipe object, connected to output pipe
        PipedInputStream inputPipe = new PipedInputStream(outputPipe);

        //Create producer and consumer threads, assiging them a pipe
        Thread producer = new Thread(new Producer(outputPipe));
        Thread consumer = new Thread(new Consumer(inputPipe));

        //Start threads
        producer.start();
        consumer.start();
    }

    //producer class that runs in its own thread
    static class Producer implements Runnable {

        //printer 
        PrintWriter prodPrinter;
        //String for telling who is waiting
        String prod = "Producer ";
        //Output stream for sending integers to consumer via pipe
        DataOutputStream output;
        int number;

        //Constructer that sets stream output to the output pipe
        //that is passed to it in the arguments
        public Producer(OutputStream out) {
            output = new DataOutputStream(out);
        }

        public void run() {
            try {
                prodPrinter = new PrintWriter("producer.txt");
            } catch (FileNotFoundException ex) {
            }

            //run 100 times
            for (int i = 0; i < 100; i++) {
                //produce random number
                number = random();
                prodPrinter.println(number);
                System.out.println("Producer: " + number);

                //Send number to consumer via pipe
                try {
                    output.writeInt(number);
                } catch (IOException e) {
                    System.out.println("Write failed");
                }

            }
            prodPrinter.close();
        }

    }

    //consumer class that runs in its own thread
    static class Consumer implements Runnable {

        PrintWriter consPrinter;
        String cons = "Consumer ";
        //input stream for receiving numbers via pipe
        DataInputStream input;
        int number;

        //Constructer that sets stream input to input pipe
        public Consumer(InputStream in) {
            input = new DataInputStream(in);
        }

        public void run() {
            try {
                consPrinter = new PrintWriter("consumer.txt");
            } catch (FileNotFoundException e) {
            }
            //run 100 times
            for (int i = 0; i < 100; i++) {
                //Reads number from input pipe
                try {
                    number = input.readInt();
                } catch (IOException ex) {
                    System.out.println("Read failed");
                }
                consPrinter.println(number);
                System.out.println("Consumer: " + number);

            }
            consPrinter.close();
        }

    }

    //method for created a random integer
    //from 0-999
    public static int random() {
        int max = 999;
        int min = 0;
        Random random = new Random();
        int randNum = random.nextInt((max - min) + 1) + min;
        return randNum;
    }

}
