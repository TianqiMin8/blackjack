import java.net.Socket; 
import java.io.IOException; 
import java.io.DataInputStream; 
import java.io.DataOutputStream;
import java.util.*;
class Card{
    private int value;
    private String suit = "";
    private String v = "";
    private String card = "";
    public Card(String t){
        char fchar = t.charAt(0);
        if (t.charAt(1) == 'S' || t.charAt(1) == 'H' || t.charAt(1) == 'C' || t.charAt(1) == 'D') {
            suit = "" + t.charAt(1);}
        else {suit = "" + t.charAt(2);value = 10;}
        if (fchar == '2' ||fchar == '3' ||fchar == '4' ||fchar == '5' ||fchar == '6' ||fchar == '7' ||fchar == '8' ||fchar == '9' ||fchar == 'J' ||fchar == 'K' ||fchar == 'Q' ||fchar == 'A' ){
            if (fchar == 'J') {value = 10;}
            else if (fchar == 'Q') {value = 10;}
            else if (fchar == 'K') {value = 10;}
            else if (fchar == 'A') {value = 11;}
            else {value = Integer.parseInt(""+fchar);}
        }
    }
    public int Value(){return value;}
    public String suit(){return suit;}
    public String toString() {return card;}
}

class Deck{ //store 364 cards
    private Card allMyCards[] = new Card[364];
    private String[] ArrayCards = new String[364]; 
    private String allValue = "";
    private int count2 = 0;
    public Deck(){
    	int count1 = 0;
    	while(count2<7) {
    		int count = 0;
	        while(count < 13){
	            allMyCards[count1] = new Card((count+2)+"S");
	            ArrayCards[count1] = allMyCards[count1].toString();
	            count++;
	            count1++;
	        }
	        while(count < 26){
	            allMyCards[count1] = new Card((count-11)+"H");
	            ArrayCards[count1] = allMyCards[count1].toString();
	            count++;
	            count1++;
	        }
	        while(count < 39){
	            allMyCards[count1] = new Card((count-24)+"C");
	            ArrayCards[count1] = allMyCards[count1].toString();
	            count++;
	            count1++;
	        }
	        while(count < 52){
	            allMyCards[count1] = new Card((count-37)+"D");
	            ArrayCards[count1] = allMyCards[count1].toString();
	            count++;
	            count1++;
	        }
	        count2++;
    	}
    }
    public String shuffle() {//change card order
    	java.util.List<String> listCards = Arrays.asList(ArrayCards);
    	Collections.shuffle(listCards);
    	String[] changedCards = listCards.toArray(new String[0]);
    	return Arrays.toString(changedCards);
    }
    public String[] getStringList(){
    	return ArrayCards;
    }
}

public class Blackjack {
	DataInputStream dis;
	DataOutputStream dos;
	static Socket socket = null;
	public Blackjack(String IpAddress, String IpPort) {
		try {
			socket = new Socket(IpAddress, Integer.valueOf(IpPort)); 
			dis = new DataInputStream(socket.getInputStream()); 
			dos = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void write(String s) throws IOException {
			dos.writeUTF(s); 
			dos.flush(); 
	} 
	private String read() throws IOException {
			return dis.readUTF(); 
	}
	static public void main(String[] args) {
		String IpAddress1 = args[0];
		String IpPort1 = args[1];
		String answer = "";
		int bet = 1;
		int money = 500;
		int score = 0;
		int dscore = 0;
		int score1;
		int score2;
		int hadvalue = 0;
		int highLowValue = 0;
		//just for testing 
		int round = 0;
		String dupCard = "";
		String mycard1 = "";
		String mycard2 = "";
		boolean betplusmoney = true;
		Blackjack blackjack = new Blackjack(IpAddress1,IpPort1);
		Boolean end = true;
		try {
			while (end) {//read the dos continously until message shows done
				String ans1 = blackjack.read();
				String[] ans = ans1.split(":");
				//just for testing
				System.out.println(ans1);
				if (ans[0].equals("login")) {
					//just for testing
					//System.out.println(ans1);
					blackjack.write("TianqiMin8:bbm");}
				else if (ans[0].equals("bet")) {
					money = Integer.parseInt(ans[1]);
					int count = 0;
					if (ans.length < 3) {bet = 1;}
					else {
						for (int i=3;i<ans.length;i++) {
							//High-Low method counting card
							Card hadcard = new Card(ans[i]);
							hadvalue = hadcard.Value(); 
							if (hadvalue>=2 && hadvalue<=6) {highLowValue += 1;}
							else if(hadvalue>=7 && hadvalue<=9) {highLowValue += 0;}
							else {highLowValue += -1;}
							System.out.print(ans[i]+" ");
							count++;
						}
						if (highLowValue*52/(364-count)<=-3) {bet = 1;}
						else if (highLowValue*52/(364-count)>-3 && highLowValue*52/(364-count)<0) {
							if(money>=2) {bet = 2;}
							else {bet = money;}
						}
						else if (highLowValue*52/(364-count)==0) {
							if(money>=3) {bet = 4;}
							else {bet = money;}
						}
						else if (highLowValue*52/(364-count)>1) {
							if(money>=5) {bet = 6;}
							else {bet = money;}
						}
						else if (highLowValue*52/(364-count)>3) {
							if(money>=10) {bet = 10;}
							else {bet = money;}
						}
						//bet = highLowValue*52/(364-count)*money; //Kelly way to bet
					}
					blackjack.write("bet:"+bet);
					System.out.println("bet:"+bet);
					money = money - bet;}
				else if (ans[0].equals("play")) {
					//just for testing
					dupCard = ans[1];
					mycard1 = ans[4];
					mycard2 = ans[5];
					Card dCard = new Card(dupCard);
					Card mCard1 = new Card(mycard1);
					Card mCard2 = new Card(mycard2);
					dscore = dCard.Value();
					score1 = mCard1.Value();
					score2 = mCard2.Value();
					score = score1 + score2;
					if (bet*2 > money) {betplusmoney = false;}
					if(score1 == score2) {
						if(score1 == 2|| score1 == 3|| score1 == 7) {
							if(dscore>=2 && dscore<=7 && betplusmoney ) {blackjack.write("split");}
							else {blackjack.write("hit");}
						}
						else if(score1 == 4) {
							if(dscore==5 || dscore==6 && betplusmoney ) {blackjack.write("split");}
							else {blackjack.write("hit");}
						}
						else if(score1 == 5) {
							if(dscore>=2 && dscore<=9 && money >= bet && betplusmoney) {blackjack.write("double");bet = bet*2;money -= bet;}
							else {blackjack.write("hit");}
						}
						else if(score1 == 6) {
							if(dscore>=2 && dscore<=6 && betplusmoney) {blackjack.write("split");}
							else {blackjack.write("hit");}
						}
						else if(score1 == 8||score1 == 11 && betplusmoney ) {
							blackjack.write("split");
						}
						else if(score1 == 9) {
							if(dscore>=2 && dscore<=9 && betplusmoney) {blackjack.write("split");}
							else {blackjack.write("stand");}
						}
						else if(score1 == 10) {
							blackjack.write("stand");
						}
					}
					else {
						if(ans[4].charAt(0)=='A'||ans[5].charAt(0)=='A') {
							if(score1==2||score2==2||score1==3||score2==3 ) {
								if(dscore==5 || dscore==6 && betplusmoney) {blackjack.write("double");bet = bet*2;money -= bet;}
								else {blackjack.write("hit");}
							}
							else if(score1==4||score2==4||score1==5||score2==5) {
								if(dscore>=4 && dscore<=6 && betplusmoney) {blackjack.write("double");bet = bet*2;money -= bet;}
								else {blackjack.write("hit");}
							}
							else if(score1==6||score2==6) {
								if(dscore>=3 && dscore<=6 && betplusmoney) {blackjack.write("double");bet = bet*2;money -= bet;}
								else {blackjack.write("hit");}
							}
							else if(score1==7||score2==7) {
								if(dscore>=3 && dscore<=6 && betplusmoney) {blackjack.write("double");bet = bet*2;money -= bet;}
								else if(dscore==2||dscore==7||dscore==8) {blackjack.write("stand");}
								else {blackjack.write("hit");}
							}
							else if(score1==8||score2==8||score1==9||score2==9) {
								blackjack.write("stand");
							}
						}
						else{
							if (score<=8 && score>=4) {blackjack.write("hit");}
							else if (score == 9) {
								if((dscore>=3 && dscore<=6) && betplusmoney) {blackjack.write("double");bet = bet*2;money -= bet;}
								else {blackjack.write("hit");}
							}
							else if (score == 10) {
								if(dscore>=2 && dscore<=9 && money >= bet && betplusmoney) {blackjack.write("double");bet = bet*2;money -= bet;}
								else {blackjack.write("hit");}
							}
							else if (score == 11) {
								if(dscore>=2 && dscore<=10 && money >= bet && betplusmoney) {blackjack.write("double");bet = bet*2;money -= bet;}
								else {blackjack.write("hit");
								}
							}
							else if (score == 12) {
								if(dscore>=4 && dscore<=6) {blackjack.write("stand");}
								else {blackjack.write("hit");}
							}
							else if (score >= 13 && score <=16) {
								if(dscore>=2 && dscore<=6) {blackjack.write("stand");}
								else {blackjack.write("hit");}
							}
							else if (score>=17) {blackjack.write("stand");}
						}
					}
				}
					
				else if (ans[0].equals("status")) {
					if (ans[3].equals("blackjack") && ans[2].equals("you")) {
						System.out.println(ans[1]+":"+ans[3]);
						score = 11;
						money += (int)(2.5*bet);						
					}
					else if(ans[2].equals("you") && ans[1].equals("lose")) {//the bust case
						System.out.println(ans[1]+":"+ans[3]);
						score = Integer.parseInt(ans[3]);
						System.out.println("Bust!");
					}
					else {
						if (ans[3].equals("blackjack")) {
							if(ans[5].equals("blackjack")) {score = 11;}
							else {score = Integer.parseInt(ans[5]);}
							System.out.println(ans[1]+":"+ans[3]);
							dscore = 11;
						if (ans[1].equals("win")) {money = money + bet*2;}
						if (ans[1].equals("lose")) {
						//just for testing
							System.out.println("Lose!");
						}
						if (ans[1].equals("push")) {money = money + bet;}}
						else {
							System.out.println(ans[1]+":"+ans[3]);
							dscore = Integer.parseInt(ans[3]);
							score = Integer.parseInt(ans[5]);
							if (ans[1].equals("win")) {money = money + bet*2;}
							if (ans[1].equals("lose")) {
							//just for testing
								System.out.println("Lose!");
							}
							if (ans[1].equals("push")) {money = money + bet;}
						}
					}
				}
				else if (ans[0].equals("done")) {
					end = false;
					System.out.println(ans[1]);
				}
				//just for testing 
				System.out.println(money);
				//just for testing 
				round += 1;
			}
			//just for testing 
			System.out.println("round"+round);
			socket.close();
		}
		catch (IOException e) {e.printStackTrace();}
		System.exit(0);
	}		
}
