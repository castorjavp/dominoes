public class Domino {
    private int topSide;
    private int bottomSide;
    public Domino(int topSide, int bottomSide){
        this.topSide = topSide;
        this.bottomSide = bottomSide;
    }

    public void reverseDomino(){
        int top = this.getTopSide();
        int bottom = this.getBottomSide();
        this.setTopSide(bottom);
        this.setBottomSide(top);
    }

    public boolean isDouble(){
        return this.getTopSide() == this.getBottomSide();
    }
    public int getTopSide(){
        return this.topSide;
    }

    public int getBottomSide(){
        return this.bottomSide;
    }

    @Override
    public String toString(){
        return this.topSide + "-" + this.bottomSide;
    }
    public void setTopSide(int topSide){
        this.topSide = topSide;
    }
    public void setBottomSide(int bottomSide){
        this.bottomSide = bottomSide;
    }

}
