package administration.tools;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Position
{
    @XmlElement
    private int x;

    @XmlElement
    private int y;

    private Position(){}

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString(){
        return "X:" + this.getX() + "\tY:" + this.getY();
    }

    public static Position getRandomPosition(){
        return new Position(
                (int) (Math.random() * 9),
                (int) (Math.random() * 9));
    }
}
