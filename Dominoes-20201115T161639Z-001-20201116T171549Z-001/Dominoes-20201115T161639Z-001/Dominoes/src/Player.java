import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Domino> hand = new ArrayList<>();

    public Player(String name){
        this.name = name;
    }
    public Player(){

    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public ArrayList<Domino> getHand(){
        return this.hand;
    }

    public int findIndexOfDoubleSix(){
        int index = 0;
        for(Domino domino : this.getHand()){
            if(domino.toString().equals("6-6")){
               return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public String toString(){
        StringBuilder dominoes = new StringBuilder();
        for(Domino currentDomino : hand) dominoes.append(currentDomino).append(" ");
        return this.name + " dominoes: " + dominoes;
    }
}