package main;

public class SwordfightPair
{
	private byte left;
	private byte right;
	private byte angle;
	
	public SwordfightPair(byte left,byte right)
	{
		this.left=left;
		this.right=right;
		this.angle=0;
	}
	
	public SwordfightPair(int left, int right)
	{
		this.left=(byte)left;
		this.right=(byte)right;
		this.angle=0;
	}
	
	public void setAngle(int angle)
	{
		setAngle((byte)angle);
	}
	
	public void setAngle(byte angle)
	{
		if(angle < 0 || angle > 3)
			throw new IllegalArgumentException("Angle must be between or equal to 0 and 3");
		this.angle=angle;
	}
	
	public byte getLeft()
	{
		return left;
	}
	
	public byte getRight()
	{
		return right;
	}
	
	public byte getAngle()
	{
		return angle;
	}
}
