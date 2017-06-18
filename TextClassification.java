import java.io.IOException;
import java.util.*;


public class TextClassification {

	public static void main(String[] args) throws IOException {

		
		String[] stopWords=DataSet.getStopWords("E:\\stop.txt");

		ArrayList<String> dataSet=new ArrayList<String>();
		String[] Folder=new String[2];
		Folder[0]="E:\\train\\spam";
		DataSet d=new DataSet();

		dataSet=d.getDataSet(Folder[0]);
		String[][][] mail=new String[2][][];
		mail[0]=new String[dataSet.size()][];
		Iterator<String> itr= dataSet.iterator();
		int i=0;
		while(itr.hasNext())
			{
				mail[0][i++]=d.getContent(Folder[0]+"\\"+itr.next()  );
				
			}
			
		/////////////////////////////////////////////////////////
		ArrayList<String> dataSet2=new ArrayList<String>();
		DataSet d2=new DataSet();
		Folder[1]="E:\\train\\ham";
		dataSet2=d2.getDataSet(Folder[1]);
		mail[1]=new String[dataSet2.size()][];
		itr= dataSet2.iterator();
		i=0;
		while(itr.hasNext())
			{
				mail[1][i++]=d.getContent(Folder[1]+"\\"+itr.next()   );
				
				
			}
		///////////////////////////////////////////////////
		ArrayList<String> vocab=new ArrayList<String>();
		vocab=getVocab(mail);
		int[] count=new int[2];
		count[1]=dataSet2.size();
		count[0]=dataSet.size();
		int countFeatures=vocab.size();
		double pHam=(double) (count[1])/(count[1]+count[0]); 
		double pSpam=(double)count[0]/(count[0]+count[0]); 	
		int[] WordCount=new int[2];
		WordCount[0]=WordCount(mail[0]);
		WordCount[1]=WordCount(mail[1]);
		double[][] pWordMail=new double[2][];
		pWordMail[1]=new double[vocab.size()];
		itr=vocab.iterator();
		int j=0;
		while(itr.hasNext())
		{	String word=itr.next();
		if(word!=null)
			pWordMail[1][j++]=((WordCount(mail[1],word)) +1.0) / ( WordCount[1]+countFeatures );
		}
		
		pWordMail[0]=new double[vocab.size()];
		itr=vocab.iterator();
		j=0;
		while(itr.hasNext())
		{	String word=itr.next();
		if(word!=null)
			pWordMail[0][j++]=((WordCount(mail[0],word)) +1.0)/( WordCount[0]+countFeatures );
			
		}
		
		/////////////////////////////////////////////////////////////
		
		
		ArrayList<String> testDataSet=new ArrayList<String>();
		String[] testMailFolder=new String[2];
		testMailFolder[0]="E:\\test\\spam";
		DataSet dTest=new DataSet();
		testDataSet=dTest.getDataSet(testMailFolder[0]);
		String[][][] testMail=new String[2][][];
		testMail[0]=new String[testDataSet.size()][];
		itr= testDataSet.iterator();
		i=0;
		while(itr.hasNext())
			{
				testMail[0][i++]=dTest.getContent(testMailFolder[0]+"\\"+itr.next()   );
			}
		
		////////////////////////////////////////////////
	
		ArrayList<String> testDataSet2=new ArrayList<String>();
		testMailFolder[1]="E:\\test\\Ham";
		DataSet dTest2=new DataSet();
		testDataSet2=dTest2.getDataSet(testMailFolder[1]);
		testMail[1]=new String[testDataSet2.size()][];
		itr= testDataSet2.iterator();
		i=0;
		while(itr.hasNext())
			{
				testMail[1][i++]=dTest2.getContent(testMailFolder[1]+"\\"+itr.next()  );
			}
		
		/////////////////////////////////////////////////////////////////////
		
		double accuracy=0;
		accuracy=test(vocab,testMail,pWordMail,pHam,pSpam,WordCount,countFeatures);
		System.out.println("accuracy="+accuracy);
		
	
		///////////////////////////////////////////////////////////////////////////////
		////////////////////////Logistic//////////////////////////////////////////////
		
		/*int[][] instance = new int[mail[0].length+mail[1].length][vocab.size()];
		int[] classFeature=new int[mail[0].length+mail[1].length];
		for(int a=0;a<2;a++)
		{
			for(i=0;i<mail[a].length;i++)
			{	String[] data=mail[a][i];
				classFeature[a*mail[0].length+i]=a;
				itr=vocab.iterator();
				int m=0;
				while(itr.hasNext())
				{
					String word=itr.next();
					instance[(a*(mail[0].length))+i][m]=(WordCount(data,word));
					m++;
				}
			
		
			}
		
		}
		
		int[][] testInstance = new int[testMail[0].length+testMail[1].length][vocab.size()+1];
		int[] testClassFeature=new int[testMail[0].length+testMail[1].length];
		for(int a=0;a<2;a++)
		{
			for(i=0;i<testMail[a].length;i++)
			{	String[] data=testMail[a][i];
				testClassFeature[a*testMail[0].length+i]=a;
				itr=vocab.iterator();
				int m=0;
				while(itr.hasNext())
				{
					String word=itr.next();
					testInstance[(a*(testMail[0].length))+i][m]=(WordCount(data,word));
					m++;
				}
				testInstance[(a*(testMail[0].length))+i][m]=1;				
		
			}
		
		}
	

		/*double[] weights=train(instance,classFeature);

		//for(i=0;i<testInstance.length;i++)			{for(j=0;j<10;j++) 				System.out.print(testInstance[i][j]+"   ");			 System.out.println("          "+i);			}
		//for printing Instances
		
		
		//LR accuracy Testing
		{
		double accuracyLR=0;
		for(i=0;i<testInstance.length;i++)		
				{
					if(testClassFeature[i]==getClass(testInstance[i],weights))
						accuracyLR++;			 
				}
		accuracyLR=accuracyLR*100.0/testInstance.length;
		System.out.println("LR accuracy="+accuracyLR);
		}
		*/
		
	}





	private static double test(ArrayList<String> vocab, String[][][] testMail,
			double[][] pWordMail, double pHam, double pSpam, int[] wordCount,
			int countFeatures)
	{
		double accuracy=0;
		int index;
		for(int a=0;a<2;a++)
		{
			
		for(int i=0;i<testMail[a].length;i++)
		{	double score1=Math.log(pHam),score2=Math.log(pSpam);
				String[] data=testMail[a][i];
				for(int j=0;j<data.length;j++)
				{	if(vocab.contains(data[j]))
						{	index=vocab.indexOf(data[j]);
							score1+=Math.log(pWordMail[1][index]);
							score2+=Math.log(pWordMail[0][index]);
						}
				else{	score1+=Math.log(( 1.0)/( wordCount[1]+countFeatures ) );
				score2+=Math.log(( 1.0)/( wordCount[0]+countFeatures ) );
			}

				}
				if(a==0)
				{			if(score1<score2)
								{accuracy++;
								}
			
				}
				else if(a==1)
					if(score1>score2)
						{accuracy++;
						
						}
		
		}

	}
		accuracy=accuracy/(testMail[0].length+testMail[1].length);
		return accuracy;
	}

	private static int WordCount(String[][] mail) {
		String[] data;
		int count=0;
		for(int i=0;i<mail.length;i++)
		{ data=new String[mail[i].length];	
		  data=mail[i];
			for(int j=0;j<data.length;j++)
			{
				count++;
			}
			
			
		}
		return count;
	}
	private static int WordCount(String[][] mail,String word) {
		String[] data;
		int count=0,i,j=0;
		for( i=0;i<mail.length;i++)
		{ data=new String[mail[i].length];	
		  data=mail[i];
			for( j=0;j<data.length  ;j++)
			{	
				if(word.equalsIgnoreCase(data[j]))
						{count++;
						}
			}
			
			
		}
		return count;
	}
	
	private static int WordCount(String[] mail,String word) {
		int count=0,i;
		for( i=0;i<mail.length;i++)
		{
				
				if(word.equalsIgnoreCase(mail[i]))
						{count++;
						}
			
			
			
		}
		return count;
	}
	
	
	private static ArrayList<String> getVocab(String[][][] mail) {

		ArrayList<String> vocab=new ArrayList<String>();
		int i=0,n=0;
		String[] data;
		for(n=0;n<2;n++)
		{
		for(i=0;i<mail[n].length;i++)
		{ data=new String[mail[n][i].length];
		  data=mail[n][i];
			for(int j=0;j<data.length&& data[j]!=null;j++)
			{
				if(!vocab.contains(data[j]))
				{
					vocab.add(data[j]);
				}
			}
			
			
		}
		}
		System.out.println("vocab="+vocab.size());
		return vocab;
	
	}
	

    private static int sigmoid(double z) {
        if(1 / (1 + Math.exp(-z))>0)
        		return 0;		//spam if w0 + sigma(Wi*Xi)>0
        else 
        		return 1;		//ham
    }

    public static double[] train(int[][] instances, int[] classFeature) {
    	double rate=0.11,lambda=1;
        double[] weights=new double[instances[0].length+1];			//weights[vocab.length]=W0;
        int ITERATIONS = 100,n;
        for ( n=0; n<ITERATIONS; n++) 
        {	
        	boolean flag=true;
        	for (int jWeight=0; jWeight<weights.length; jWeight++) 

        	{
        			double wRate=0;
        			for (int i=0; i<instances.length; i++) 
        				{
        						int[] x = new int[instances[i].length+1];
        						for(int m=0;m<instances[i].length;m++)
        								x[m]=instances[i][m];
        						x[instances[i].length]=1;
        						double predicted = getClass(x,weights);
        						wRate+=(classFeature[i] - predicted) * x[jWeight];
            		
        				}
        			double delta= rate *( wRate-lambda*weights[jWeight]);
        			weights[jWeight] = weights[jWeight] +delta;
        			if(delta/weights[jWeight]>0.0001)
        				flag=false;
        	}
         
           if(flag)
           {
        	   break;
           }
           System.out.println("iterations="+n);

        }
      	for(int j=0;j<weights.length;j++)
    		System.out.print( "   " + weights[j]);
      	System.out.println();
      	System.out.println(n+"iterations for no change");
        return weights;
    }

     static int getClass(int[] x,double[] weights) {
        double logit = .0;
        int i;
        for (i=0; i<weights.length;i++)  {
            logit += weights[i] * x[i];
        }
        return sigmoid(logit);
    }
    

}

