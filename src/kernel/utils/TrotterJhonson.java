package kernel.utils;

import java.math.BigInteger;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class TrotterJhonson {
	private int tam; // tamanho da instancia
	private List<Integer> currentPermutation; // permutation corrente
	private BigInteger maxPerm;
	private BigInteger rank;
	
	public TrotterJhonson(int n) {
		this.tam = n;
		this.currentPermutation = new IntArrayList();
		this.maxPerm = factorial(BigInteger.valueOf(n));
		for(int i=1,j=1;i<=this.tam;i++,j++) {
			this.currentPermutation.add(j);
		}
		this.setRank(BigInteger.valueOf(TrotterJohnsonRanking(currentPermutation)));
	}
	
	public int getTam() {
		return this.tam;
	}
	public BigInteger getMaxPerm() {
		return this.maxPerm;
	}

	public void setPerm(List<Integer> permutacao,BigInteger ranking) {
		this.tam = permutacao.size();
		this.currentPermutation = permutacao;
		this.maxPerm = factorial(BigInteger.valueOf(this.tam));
		this.setRank(ranking);
	}
	
	public int TrotterJohnsonRanking(List<Integer> atual) {
		
		int r = 0,k=0,i=0,j;
		for(j=2;j<=this.tam;j++) {
			k=0;
			i=0;
			while(atual.get(i) != j) {
				if(atual.get(i) < j) { 
					k = k+1;
				}
				i=i+1;
			}
			if(r%2 == 0) {
				r = r*j + j - k-1;
			}else {
				r = j*r + k;
			}
		}
		
		return r;
	}
	
	public List<Integer> TrotterJohnsonUnranking( BigInteger rank) {
		this.currentPermutation = new IntArrayList();
		for(int n=0;n<this.tam;n++) {
			this.currentPermutation.add(n);
		}
		this.currentPermutation.set(0,1);
		int r2=0,k=0;
		double r1=0;
		for(int j=2;j<=this.tam;j++) { 
			BigInteger aux = (rank.multiply(factorial(BigInteger.valueOf(j))));
			
			r1 =(aux.divide(factorial(BigInteger.valueOf(this.tam)))).intValue(); 
			k =(int) r1- j*r2;
			if(r2 %2 == 0) {
				for(int i=j-1;i>=j-k;i--) {
					this.currentPermutation.set(i, this.currentPermutation.get(i-1));
				}
				this.currentPermutation.set(j-k-1,j);
			}else {
				for(int i=j-1;i >=k+1;i--) {
					this.currentPermutation.set(i,this.currentPermutation.get(i-1));
				}
				this.currentPermutation.set(k,j);
			}
			r2=(int)r1;
		}
		
		return this.currentPermutation;
	}
	// calcula se uma permutação é par ou impar
	// ele tenta transformar de 1 para n a permutacao corrente
	// se o numero de vezes necessario apra que isso ocorra for impar
	// a permutacao é impar. O contrario para permutacoes pares
	private int pariedade(List<Integer> array,int size) {
		boolean[] flags = new boolean[size];
	    int i, j, c=0;
	    for (i=0; i<size; i++){
	        flags[i]=false;
	    }
	    for (j=1; j<=size; j++){
	        if (!flags[j-1]){
	            c++;
	            flags[j-1]=true;
	            i=j;
	            while (array.get(i-1)!=j){
	                i=array.get(i-1);
	                flags[i-1]=true;
	            }
	        }
	    }
	    return (size-c)%2;
	}
	
	private IntArrayList TrotterJohnsonSucessor(List<Integer> Pi) {
		int st=0;
		boolean done;
		int d,par;
		int temp;
		List<Integer> p = new IntArrayList();
		IntArrayList retorno = new IntArrayList();
		for(int n=0;n<this.tam;n++) {
			p.add(Pi.get(n));
		}
		done = false;
		int m=this.tam; // m recebe o tamanho do vetor de 1 até n pois 0 nao conta
		while(m > 1 && !done) { // enquanto m for maior que 1 ou nao tiver terminado a permutacao
			d=1;
			while(p.get(d-1) != m) { 
				d=d+1;
			}
			for(int i=d;i<m;i++) {
				p.set(i-1,p.get(i));
			}
			par=pariedade(p,m-1);
			
			if(par==1) {
				if(d == m) {
					m = m-1;
				}else {
					temp=Pi.get(st+d-1);
					Pi.set(st+d-1,Pi.get(st+d));
					Pi.set(st+d,temp);
					done = true;
					retorno.add(st+d-1); // de onde saiu
					retorno.add(st+d); // pra onde foi
					return retorno;
				}
			}else {
				if(d==1) {
					m=m-1;
					st=st+1;
				}else {
					temp=Pi.get(st+d-1);
					Pi.set(st+d-1,Pi.get(st+d-2));
					Pi.set(st+d-2,temp);
					done = true;
					retorno.add(st+d-1);// de onde saiu
					retorno.add(st+d-2);// pra onde foi
					return retorno;
				}
			}
		}
		if(m == 1) {
			System.out.println("fim das permutacoes");
			return retorno;
		}
		return retorno;
	}
	
	
	
	public IntArrayList NextPermutation() {
		this.rank = rank.add(BigInteger.ONE);
		return TrotterJohnsonSucessor(this.currentPermutation);
	}
	public int getCurrentRanking() {
		return TrotterJohnsonRanking(this.currentPermutation);
	}
	public List<Integer> getCurrentPermutation() {
		return this.currentPermutation;
	}
	public void printCurrentPermutation() {
		System.out.print(this.currentPermutation);
	}
	public void printLnCurrentPermutation() {
		System.out.print(this.currentPermutation+"\n");
}
	

	private BigInteger factorial(BigInteger i) {
        if (i == BigInteger.ZERO || i == BigInteger.ONE) {
            return BigInteger.ONE;
        } else {
            return i.multiply(factorial(i.subtract(BigInteger.ONE)));
        }
    }

	public BigInteger getRank() {
		return rank;
	}

	public void setRank(BigInteger rank) {
		this.rank = rank;
		this.currentPermutation = TrotterJohnsonUnranking(rank);
	}

}
