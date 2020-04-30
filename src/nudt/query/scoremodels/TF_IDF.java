/*
 * Terrier - Terabyte Retriever 
 * Webpage: http://ir.dcs.gla.ac.uk/terrier 
 * Contact: terrier{a.}dcs.gla.ac.uk
 * University of Glasgow - Department of Computing Science
 * http://www.gla.ac.uk/
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is TF_IDF.java.
 *
 * The Original Code is Copyright (C) 2004-2009 the University of Glasgow.
 * All Rights Reserved.
 *
 * Contributor(s):
 *   Ben He <ben{a.}dcs.gla.ac.uk> (original author)
 *   Gianni Amati <gba{a.}fub.it> 
 *   Vassilis Plachouras <vassilis{a.}dcs.gla.ac.uk>
 */
package nudt.query.scoremodels;
/**
 * TF_IDF weighting model based on the implementation in Terrier
 * @author <a href="jiangkun@nudt.edu.cn">Kun Jiang</a>
 * @since 2014-5-26
 */
public class TF_IDF extends WeightingModel {

	public static final double k_1 = 1.2d;
	public static final double b = 0.75d;

	public TF_IDF() {
		super();
	}

	/**
	 * Returns the name of the model, in this case "TF_IDF"
	 * @return the name of the model
	 */
	public final String getInfo() {
		return "TF_IDF";
	}
	/**
	 * Uses TF_IDF to compute a weight for a term in a document.
	 * @param tf The term frequency of the term in the document
	 * @param docLength the document's length
	 * @return the score assigned to a document with the given 
	 *		 tf and docLength, and other preset parameters
	 */
	public final double score(double tf, double docLength) {
		double Robertson_tf = k_1*tf/(tf+k_1*(1-b+b*docLength/averageDocumentLength));
		double idf = log(numberOfDocuments/documentFrequency+1);
		return keyFrequency * Robertson_tf * idf;
	}
	
	public double precompute() {
		double idf = log(numberOfDocuments/documentFrequency+1);
		return keyFrequency *idf;
	}
	
	public double score(double tf, double docLength, double precomputed) {
		double Robertson_tf = k_1*tf/(tf+k_1*(1-b+b*docLength/averageDocumentLength));
		return Robertson_tf* precomputed;
	}

	/**
	 * Uses TF_IDF to compute a weight for a term in a document.
	 * @param tf The term frequency of the term in the document
	 * @param docLength the document's length
	 * @param documentFrequency The document frequency of the term (ignored)
	 * @param termFrequency the term frequency in the collection (ignored)
	 * @param keyFrequency the term frequency in the query (ignored).
	 * @return the score assigned by the weighting model TF_IDF.
	 */
	public final double score(
		double tf,
		double docLength,
		double documentFrequency,
		double termFrequency,
		double keyFrequency) 
	{
		double Robertson_tf = k_1*tf/(tf+k_1*(1-b+b*docLength/averageDocumentLength));
		double idf = log(numberOfDocuments/documentFrequency+1);
		return keyFrequency*Robertson_tf * idf;

	}

	public static void main(String args[]){
		WeightingModel tfidf = new TF_IDF();
		tfidf.setKeyFrequency(1);
		tfidf.setDocumentFrequency(10);
		tfidf.setTermFrequency(100);
		tfidf.setNumberOfDocuments(10000);
		tfidf.setAverageDocumentLength(50);
		double pre = tfidf.precompute();
		System.out.println(tfidf.score(100, 200));
		System.out.println(tfidf.score(100, 200, pre));
		System.out.println(tfidf.score(100, 200, 10, 100, 1));
	}

}
