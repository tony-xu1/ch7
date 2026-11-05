import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class StockTrader{
	public static void main (String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(new File("tiny.txt"));
        String symbol = in.next();
        int n = in.nextInt();
        
        int[] years = new int[n];
        int[] months = new int[n];
        double[] prices = new double[n];
   
        
        loadData(in, years, months, prices);
        printData(years, months, prices);
        double drop = (prices[indexOfBiggestDrop(prices) - 1] - prices[indexOfBiggestDrop(prices)])/prices[indexOfBiggestDrop(prices) - 1];
        
        //System.out.printf(symbol + " lowest: $%.2f in %s %d\n", prices[indexOfLowest(prices)], monthName(months[indexOfLowest(prices)]), years[indexOfLowest(prices)]);
        //System.out.printf(symbol + " highest: $%.2f in %s %d\n", prices[indexOfHighest(prices)], monthName(months[indexOfHighest(prices)]), years[indexOfHighest(prices)]);
        //System.out.printf(symbol + " biggest drop: %.2f in %s %d\n", drop, monthName(months[indexOfBiggestDrop(prices)]), years[indexOfBiggestDrop(prices)]);
		
		double result = simulate(years, months, prices);
		System.out.printf("Final value: $%, .2f%n", result);

	}
	
	public static void loadData(Scanner in, int[] years, int[] months, double[] prices){
		for(int i = 0; i < years.length; i++){
			years[i] = in.nextInt();
			months[i] = in.nextInt();
			prices[i] = in.nextDouble();
		}
	}
	
	public static String monthName(int month){
		String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		return months[month - 1];
	}
	
	public static void printData(int[] years, int[] months, double[] prices){
		for(int i = 0; i < years.length; i++){
			//System.out.printf("%s %d: %.2f\n", monthName(months[i]), years[i], prices[i]);
		}
	}
	
	public static int indexOfLowest(double[] prices){
		double lowest = prices[0];
		int index = 0;
		for (int i = 0; i < prices.length; i++){
			if (prices[i] < lowest){
				lowest = prices[i];
				index = i;
			}
		}
		return index;
	}
	
	public static int indexOfHighest(double[] prices){
		double highest = 0;
		int index = 0;
		for (int i = 0; i < prices.length; i++){
			if (prices[i] > highest){
				highest = prices[i];
				index = i;
			}
		}
		return index;
	}
	
	public static int indexOfBiggestDrop(double[] prices){
		double drop = 0;
		int index = 1;
		for (int i = 1; i < prices.length; i++){
			if (prices[i - 1] - prices[i] > 0 && (prices[i - 1] - prices[i])/prices[i - 1] > drop){
				drop = (prices[i - 1] - prices[i])/prices[i - 1];
				index = i;
			}
		}
		return index;
	}
	
	public static final double START_CASH = 10000.0;
	public static final double FEE = 10.0;

	public static double simulate(int[] years, int[] months, double[] prices) {
		double cash = START_CASH;
		int shares = 0;
		double boughtAt = 0.0;

		for (int i = 0; i < prices.length; i++) {
			double[] history = Arrays.copyOf(prices, i + 1);
			String action = decide(history, shares > 0, boughtAt);

			if (action.equals("BUY") && shares == 0) {
				int canBuy = (int) ((cash - FEE) / prices[i]);
				if (canBuy > 0) {
					shares = canBuy;
					cash = cash - shares * prices[i] - FEE;
					boughtAt = prices[i];
					System.out.printf("%s %d: BUY %d shares at $%.2f%n",
                        monthName(months[i]), years[i], shares, prices[i]);
				}
			} else if (action.equals("SELL") && shares > 0) {
            cash = cash + shares * prices[i] - FEE;
            System.out.printf("%s %d: SELL %d shares at $%.2f%n",
                    monthName(months[i]), years[i], shares, prices[i]);
            shares = 0;
            boughtAt = 0.0;
			}
		}
		return cash + shares * prices[prices.length - 1];
	}
	
	public static String decide(double[] history, boolean holding, double boughtAt){
		return dipBuyer(history, holding, boughtAt);
	}
	
	public static String buyAndHold(double[] history, boolean holding, double boughtAt){
		return "BUY";
	}
	
	public static String dipBuyer(double[] history, boolean holding, double boughtAt){
		if (!holding){
			for (int i = history.length - 3; i < history.length; i++){
				if (history.length >= 3 && history[i] > history[i + 1] && history[i + 1] > history[i + 2]){
					boughtAt = history[i + 2];
					return "BUY";
				}
			}
		} else if (holding){
				if(boughtAt * 1.1 < history[history.length - 1]){
					return "SELL";
				}
			}
		return "HOLD";
	}
	
	public static String movingAverage(double[] history, boolean holding, double boughtAt){
		double sum = 0;
		int n = 0;
		double avg = 0;
		for (int i = 0; i < history.length; i++){
			if (i < 12){
				sum += history[i];
				return "HOLD";
			} else if(i >= 12){
				sum = sum + history[i] - history[n];
				n++;
				avg = sum/12;
				System.out.println("ran");
			}
			if (!holding && history[i] >= avg){
				boughtAt = history[i];
				System.out.println("ran");
				return "BUY";
			} else if (holding && boughtAt <= avg){
				return "SELL";
			}
		}
		return "HOLD";
	}
}
