import java.util.ArrayList;
class InformationGain
{

	// calculate entropy for two categories.
	static double twoCEntropy(double cProb)
	{
		if(cProb == 0 || cProb == 1) return 0.0;
		else{
			return - ( cProb * log2(cProb) ) - ( (1.0 - cProb) * log2(1.0 - cProb));
		}
	}

	// log with base 2
	static double log2(double p)
	{
		return Math.log(p) / Math.log(2);
	}

	static double[] calculateIG(ArrayList<int[]> table)
	{
		int numOfExamples = table.size(); // get the num of examples
		int numOfFeatures = table.get(0).length-1; // get the num of features (last column is C)

		double[] IG = new double[numOfFeatures]; // IG for every feature

		int positives = 0;
		for (int i = 0; i < numOfExamples; i++)
		{
			if(table.get(i)[numOfFeatures] == 1) positives++; // count how many are C=1
		}

		// We need all of the below for the IG formula


		double PC1 = (double) positives / numOfExamples; // P(C=1)
		double HC = twoCEntropy(PC1); // H(C)

		// P(X=1) --> prob. of X=1
		// P(X=0) --> 1 - P(X=1)
		double[] PX1 = new double[numOfFeatures];

		// P(C=1|X=1)
		// P(C=0|X=1) = 1 - P(C=1|X=1)
		double[] PC1X1 = new double[numOfFeatures];

		// P(C=1|X=0)
		// P(C=0|X=0) = 1 - P(C=1|X=0)
		double[] PC1X0 = new double[numOfFeatures];

		// H(C=1|X=1)
		double[] HCX1 = new double[numOfFeatures];

		// H(C=1|X=0)
		double[] HCX0 = new double[numOfFeatures];

		for (int j = 0; j < numOfFeatures; j++)
		{
			int cX1 = 0; // for every feature, count the examples in which X=1
			int cC1X1 = 0; // count how many examples are C=1 given X=1
			int cC1X0 = 0; // count how many examples are C=1 given X=0
			for (int i = 0; i < numOfExamples; i++)
			{
				if(table.get(i)[j] == 1) cX1++;
				if(table.get(i)[j] == 1 && table.get(i)[numOfFeatures] == 1) cC1X1++;
				if(table.get(i)[j] == 0 && table.get(i)[numOfFeatures] == 1) cC1X0++;
			}

			PX1[j] = (double) cX1 / numOfExamples; // P(X=1) for j-th feature.

			// if all examples have X=0
			if(cX1 == 0) PC1X1[j] = 0.0; // no example has X=1 so P(C=1|X=1) = 0
			else PC1X1[j] = (double) cC1X1 / cX1; // dividing by how many examples have X=1 in general

			// if all examples have X=1
			if(cX1 == numOfExamples) PC1X0[j] = 0.0; // no example has X=0 so P(C=1|X=0) = 0
			else PC1X0[j] = (double) cC1X0 / (numOfExamples - cX1); // dividing by how many examples have X=0 in general

			HCX1[j] = twoCEntropy(PC1X1[j]); // entropy for the category when X=1 (for j-th feature)
			HCX0[j] = twoCEntropy(PC1X0[j]); // entropy for the category when X=0 (for j-th feature)

			IG[j] = HC - ( (PX1[j] * HCX1[j]) + ( (1.0 - PX1[j]) * HCX0[j]) ); // IG formula (see slides)

		}

		return IG;
	}


}