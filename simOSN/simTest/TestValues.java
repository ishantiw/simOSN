package simTest;

import java.util.Scanner;

public class TestValues {

	public static void main(String[] args) {
		int network_size = 50000;
		double max = 100.0;
		double min = 1.0;
		double step = (max - min) / (50000 - 1);
		double peakValue = 10000.0;
		System.out.println("Value of Step is "+ step);
		TestValues test = new TestValues();
		int option = 3;
		while(option!=0){
			Scanner reader = new Scanner(System.in); 
			System.out.println("Please enter 1 for Linear distribution \n "
					+ "enter 2 for peak distribution "
					+ "\n enter 0 to exit ");
			option = reader.nextInt(); 
			switch (option) {
			case 1:
				test.linearDistributionDryRun(network_size, max, min, step);
				break;
			case 2:
				test.peakDistributionDryRun(network_size, peakValue);
				break;
			case 0:
				System.out.println("Exiting the system...\n Bye");
				System.exit(0);
			}
		}
        
	}
	//Dry run for Linear Distribution
	public void linearDistributionDryRun(int network_size, double max, double min, double step){
		double sum = 0.0;
        double tmp;
        System.out.println("####Linear distribution####");
		for (int i = 0; i < network_size; ++i) {
            tmp = i * step + min;
            sum += tmp;
            if(i<100){//Limiting display results upto 100 records for simplicity
            	//System.out.println("i:"+i+",Step:"+step+",step part "+i*step);
            	System.out.println("Node "+i+" Value is :"+ tmp);
            	}
        	}
        System.out.println("Sum is "+sum+" and the average is "+sum/network_size );
	}
	
	//Dry run for Peak Distribution
	public void peakDistributionDryRun(int network_size, double value){
		double sum = 0.0;
        double tmp = 0.0;
        System.out.println("####Peak distribution####");
        for (int i = 0; i < network_size; i++) {
        	sum += tmp;
        	tmp = 0;
        	if(i==0){
        		tmp = value;
        	}else{
        		tmp = 0;
        	}
        	if(i<100)//Limiting display results upto 100 records for simplicity
            System.out.println("Node "+i+" value: "+tmp);
        }
        System.out.println("Sum is "+sum+" and the average is "+sum/network_size );
	}
}
