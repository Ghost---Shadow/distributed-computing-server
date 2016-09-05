package org.distributed.serializable;

import java.io.Serializable;

public class Task implements Serializable {
	private static final long serialVersionUID = 1L;
	public int type, id;
	public float[][] p, q, r;

	public Task(int type, int id, float[][] p, float[][] q, float[][] r) {
		this.type = type;
		this.id = id;
		this.p = p;
		this.q = q;
		this.r = r;
	}
	
	public void print(){
		//printMatrix(p);
		//printMatrix(q);
		printMatrix(r);
	}
	
	private void printMatrix(float[][] x){
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[i].length; j++) {
				System.out.println(x[i][j] + " ");
			}
			System.out.println();
		}
	}
}
