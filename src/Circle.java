
public class Circle
{
   private int centerX;
   private int centerY;
   
   
   public Circle (int inX, int inY)
   {
	   centerX = inX;
	   centerY = inY;
	   
   }
public int getCenterX()
{
	return centerX;
}
public void setCenterX(int centerX)
{
	this.centerX = centerX;
}
public int getCenterY()
{
	return centerY;
}
public void setCenterY(int centerY)
{
	this.centerY = centerY;
}
@Override
public String toString()
{
	return "Circle [centerX=" + centerX + ", centerY=" + centerY + "]";
}
   
   
}
