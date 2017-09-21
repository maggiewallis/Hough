import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class HoughPanel extends JPanel
{
	BufferedImage sourceImg,edgeImg, houghImg, resultImg;
	ArrayList<Circle> foundCircles;
	public HoughPanel()
	{
		super();
		int[][][] RGBSource = ImageManager.RGBArrayFromFile("dots&cards.jpg");
		sourceImg = ImageManager.ImageFromArray(RGBSource);
		
		int[][] grayEdges = findEdges(ImageManager.toGrayArray(RGBSource));
		edgeImg = ImageManager.ImageFromArray(grayEdges);
		
		int[][] houghArray = generateHough(grayEdges);
		// Note: houghArray is not an image array - it has numbers that might go much 
		//       higher than 255 (or it might be much lower). Need to multiply it 
		//       by a factor so that the just the largest number is 255 before we can
		//       display that. (keep the houghArray the same and make a modified 
		// 		 copy.)
		//       This is called "normalizing" the array.
		houghImg = ImageManager.ImageFromArray(normalizeArrayTo255(houghArray));
		
		foundCircles = findBestCircles(houghArray);
		
		int[][][] resultArray = buildResult(RGBSource,foundCircles);
		resultImg = ImageManager.ImageFromArray(resultArray);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		int w = sourceImg.getWidth();
		int h = sourceImg.getHeight();
		g.drawImage(sourceImg, 0, 0, null);
		g.drawImage(edgeImg, w, 0, null);
		g.drawImage(houghImg, 0, h, null);
		g.drawImage(resultImg, w, h, null);
		
		g.setColor(Color.BLACK);
		g.drawString("Original", 3, 12);
		g.drawString("Edges", w+3, 12);
		g.drawString("Hough Map", 3, 12+h);
		g.drawString("Result", w+3, h+12);
		
		g.setColor(Color.WHITE);
		g.drawString("Original", 2, 11);
		g.drawString("Edges", w+2, 11);
		g.drawString("Hough Map", 2, 11+h);
		g.drawString("Result", w+2, h+11);
		
		for (Circle c: foundCircles)
		{
			g.setColor(Color.RED);
			g.drawOval(w+c.getCenterX()-27,h+c.getCenterY()-27,54,54);
		}
	}
	
	
	public int[][] findEdges(int[][] sourceArray)
	{
		double deltaSquaredThreshold = 800; // if dx^2 + dy^2 > threshold, call it an edge.
		
		int[][] edgeArray = ImageManager.createGrayscaleArrayOfSize(sourceArray.length-1, 
																	sourceArray[0].length-1);
		// TODO: insert your code here.
		
		for (int i = 1; i < sourceArray.length - 1; i++) 
		{
			for (int j = 1; j < sourceArray[0].length - 1; j++) 
			{
				int Dx = sourceArray[i][j] - sourceArray[i - 1][j];
				int Dy = sourceArray[i][j] - sourceArray[i][j - 1];
				int magnitude = (int) (Math.pow(Dx, 2) + Math.pow(Dy, 2));
				if (magnitude > 800)
				{
					edgeArray[i][j] = 255;
				}
				else
				{
					edgeArray[i][j] = 0;
				}
			}
		}
		
		
		//-------------------------------------------------
		return edgeArray;
	}
	

	public int[][] generateHough(int[][] grayEdges)
	{
	    int TARGET_RADIUS = 27;
	    
		int[][] houghArray = new int[grayEdges.length][ grayEdges[0].length];
		
	  
		for (int i = 1; i < grayEdges.length; i++) 
		{
			for (int j = 1; j < grayEdges[0].length; j++) 
			{
				if (grayEdges[i][j] == 255)
				{
					for (int k = i - 27; k < i + 28; k++)
					{
						for (int l = j - 27; l < j + 28; l++)
						{
							if (k >= 0 && k < grayEdges.length)
								if (l >= 0 && l < grayEdges[0].length)
								{
									int X = i - k;
									int Y = j - l;
									int magnitude = (int)(Math.pow(X, 2) + Math.pow(Y, 2));
									if ((int)Math.sqrt(magnitude) == 27)
									{
										houghArray[k][l] += 1;
									}
								}
						}
					}
				}
			}
		}
	    // ----------------------------
		return houghArray;
	}
	
	public ArrayList<Circle> findBestCircles(int [][] houghArray)
	{
		int maxNumCirlces = 10; //the most circles you're hoping to get (adjust this)
		int votesThreshold = 60; // the minimum number of votes required to count as a "found" circle.
		int annihilationRadius = 15; // after you find a maximum in the hough array (and presumably do something with it),
        // wipe out all the votes within this radius of the vote winner to zero, so that
        // you are ready to get the next maximum. (Finding two maxima within a couple of pixels
        // is unlikely to be useful and more likely to be natural/rounding error.)
		int[][] houghCopy = ImageManager.deepCopyArray(houghArray);
		ArrayList<Circle> listOfCircles = new ArrayList<Circle>();
		
		
		
		int max = 0;
		int maxX = 0;
		int maxY = 0;
		
		while (listOfCircles.size() < maxNumCirlces)
		{
			max = 0;
			maxX = 0;
			maxY = 0;
			for (int i = 1; i < houghArray.length; i++) 
			{
				for (int j = 1; j < houghArray[0].length; j++) 
				{
					if (houghArray[i][j] > max) 
				    {
				      max = houghArray[i][j];
				      maxX = j;
				      maxY = i;
				    }
				}
			}
			if (max > votesThreshold)
				{
					listOfCircles.add(new Circle(maxX,maxY));
				}
			else
				break;
			for (int j = maxX -15; j < maxX + 15; j++) 
			{
				for (int i = maxY - 15; i < maxY + 15; i++) 
				{
				    int Dx = maxX - j;
				    int Dy = maxY - i;
					int magnitude = (int)(Math.pow(Dx, 2) + Math.pow(Dy, 2));
					if(sqrt(magnitude) < annihilationRadius)
					{
						houghArray[i][j] = 0;
					}
				}
			}
		}		
	
		
		
		//---------------------------
		return listOfCircles;
	}
	
	private int sqrt(int magnitude) {
		return 0;
	}

	
	public int[][][] buildResult(int [][][] RGBSource, ArrayList<Circle> circleList)
	{
		int[][][] result = ImageManager.deepCopyArray(RGBSource);
		// for each location in the circle list, set the corresponding pixel in result to be red (255,0,0).
		
		
		//------------
		return result;
	}
	
	public int[][] normalizeArrayTo255(int[][] unnormalized)
	{
		int max =0;

		for (int i = 1; i < unnormalized.length; i++) 
		{
			for (int j = 1; j < unnormalized[0].length; j++) 
			{
				if (unnormalized[i][j] > max) 
			    {
			      max = unnormalized[i][j];
			    }
			}
		}
		
		//-----------------------------------------
		if (max == 0)
			throw new RuntimeException("Could not normalize the array to 0 to 255; array was empty.");
		int [][] normalized = new int[unnormalized.length][unnormalized[0].length];
		
		for (int i = 1; i < unnormalized.length; i++) 
		{
			for (int j = 1; j < unnormalized[0].length; j++) 
			{
				normalized[i][j] = (unnormalized[i][j]*255)/max;
			}
		}
			
		//
		return normalized;
	}
}
