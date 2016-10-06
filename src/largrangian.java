import java.util.*;
import java.io.*;
import java.lang.Integer;

public class largrangian {
	public static final int MAX_ITER = 350;
	public static final int MAX_REP  = 20;
	public static final double EPS  = 0.00001;
	
	
	public static void main(String[] argv) throws FileNotFoundException {
		int datan, I, J;
		System.out.println(System.getProperty("user.dir"));
		String fileName = "./src/test2.txt";
		String outfile = "./src/test2out.txt";
		String drawfile = "./src/test2draw.txt";
		File input_file = new File(fileName);
		File output_file = new File(outfile);
		File draw_file = new File(drawfile);
		//Scanner scanner = new Scanner(System.in);
		Scanner scanner = new Scanner(input_file);
		PrintWriter output = new PrintWriter(output_file);
		PrintWriter output2 = new PrintWriter(draw_file);
		datan = scanner.nextInt();
		List<Double> lowerbound = new ArrayList<Double>();
		List<Double> GAP = new ArrayList<Double>(); 
		for(int ncount = 0; ncount < datan; ncount++){
			System.out.println(ncount);
			I = scanner.nextInt();
			output.println("[I] Size of customer set:" + I);
			System.out.println("[I] Size of customer set:" + I);
			J = scanner.nextInt();
			output.println("[J] Size of facility set:" + J);
			
			List<Double> f_j = new ArrayList<Double>();
			List<Integer> k_j = new ArrayList<Integer>();
			List<Integer> h_i = new ArrayList<Integer>();
			List<List<Double>> d_ji = new ArrayList<List<Double>>();
		
			output.println("[h_i] demand quantity of customer i:");
			for(int a=0;a<I;a++){
				h_i.add(scanner.nextInt());
			}
			output.println(h_i);
			output.println("[f_j] fixed cost to build facility j:");
			for(int a=0;a<J;a++){
				f_j.add(scanner.nextDouble());
			}
			output.println(f_j);
			output.println("[k_j] capacity of facility at j:");
			for(int a=0;a<J;a++){
				k_j.add(scanner.nextInt());
			}
			output.println(k_j);
			output.println("[d_ij] cost to ship a unit of product from location j to customer i:");
			for(int a=0;a<J;a++){
				ArrayList<Double> d_j = new ArrayList<Double>();
				for(int b=0;b<I;b++){
					d_j.add(scanner.nextDouble());
				}
				d_ji.add(d_j);
			}
			for(int a=0;a<J;a++){
				List<Double> d_j = d_ji.get(a);
				output.println(d_j);
			}
			List<Double> siteX = new ArrayList<Double>();
			List<Double> siteY = new ArrayList<Double>();
			List<Double> facilityX = new ArrayList<Double>();
			List<Double> facilityY = new ArrayList<Double>();
			for(int i = 0; i < I; i++){
				siteX.add(scanner.nextDouble());
				siteY.add(scanner.nextDouble());
			}
			for(int j = 0; j < J; j++){
				facilityX.add(scanner.nextDouble());
				facilityY.add(scanner.nextDouble());
			}
			output.println("[SiteX] :");
			output.println(siteX);
			output.println("[SiteY] :");
			output.println(siteY);
			output.println("[FacilityX] :");
			output.println(facilityX);
			output.println("[FacilityY] :");
			output.println(facilityY);
			List<Double> lambda_i = new ArrayList<Double>(Collections.nCopies(I, (Double) 0.0));
		
			double L = 0.0,L_old=0.0;
			double delta = 100.0;
			int count=0,step=0;
			
			List<Integer> sol_X;
			List<List<Integer>> sol_Y;
			List<Double> lambda_old = new ArrayList<Double>();
			//start subgradient
			while(true){
				if(count >20){
					delta = delta/2.0;
					count =0;
				}
				System.out.println("step:" +step +", lambda:" +lambda_i);
				List<Integer> X_j = new ArrayList<Integer>();
				List<List<Integer>> Y_ji = new ArrayList<List<Integer>>();
				int K;
				//solve j knapsack problem
				double[] LL = new double[2]; 
				for(int j=0;j<J;j++){
					List<Integer> Y_i = SolveDP(I,J,j,h_i,k_j.get(j),f_j.get(j),lambda_i,d_ji,LL);
					Y_ji.add(Y_i);
					int check_X=0;
					for(int y : Y_i){
						check_X += y;
					}
					if(check_X == 0){
						X_j.add(0);
					}
					else{
						X_j.add(1);
					}
					//the ending dp table would be [i+1][k]
				}
				L = 0.0;
				for(int j = 0; j < J; j++){
					L = L + f_j.get(j)* (double)X_j.get(j);
					for(int i=0; i<I; i++){
						L = L + (double)Y_ji.get(j).get(i) * h_i.get(i) * d_ji.get(j).get(i);
					}
				}
				for(int i=0; i<I; i++){
					int Ysum = 0;
					for(int j=0; j<J; j++){
						Ysum =Ysum + Y_ji.get(j).get(i);
					}
					L = L + (lambda_i.get(i) * (1.0-(double)Ysum));
				}
				//System.out.println("printL: " + L);
				System.out.println(L);
				if( L < L_old){
					delta = delta/2.0;
				}
				else{
					lambda_old.clear();
					lambda_old.addAll(lambda_i);
				}
				if(step > MAX_ITER){
					//solution
					sol_X = new ArrayList<Integer>(X_j);
					sol_Y = new ArrayList<List<Integer>>(Y_ji);					
					lowerbound.add(L);			
					break;
				}
				//calculate subgradient & lambda_new
				for(int i=0;i<I;i++){
					int sum_Y_ji=0;
					for(int j=0;j<J;j++){
						sum_Y_ji += Y_ji.get(j).get(i);
					}
					lambda_i.set(i,lambda_old.get(i)+delta *(1 - sum_Y_ji));
				}
				if(step!=0){
					if(((L-L_old)/L < EPS) && L > L_old ){
					//reduce step size
						sol_X = new ArrayList<Integer>(X_j);
						sol_Y = new ArrayList<List<Integer>>(Y_ji);					
						lowerbound.add(L);			
						break;
					}
					else if((L-L_old)/L < EPS){
						delta/= 2.0;
						count ++;
						if(count >20){
							System.out.println("count > 20");
							sol_X = new ArrayList<Integer>(X_j);
							sol_Y = new ArrayList<List<Integer>>(Y_ji);					
							lowerbound.add(L);			
							break;
						}
					}
				}
				L_old = L;
				step ++;
			}
			System.out.println(step+" "+delta+"end");
			Map<String, List<List<Integer>>> backMap = CAPBACKUP(I, J, h_i, d_ji, f_j, k_j, sol_Y, sol_X);
			List<List<Integer>> Y = backMap.get("Y");
			List<List<Integer>> X_temp = backMap.get("X");
			List<Integer> X = X_temp.get(0);
			List<List<Double>> coorX = new ArrayList<List<Double>>();
			List<List<Double>> coorY = new ArrayList<List<Double>>();
			for(int j = 0; j < J; j++){
				List<Double> Xtemp = new ArrayList<Double>();
				List<Double> Ytemp = new ArrayList<Double>();
				Xtemp.add(facilityX.get(j));
				Ytemp.add(facilityY.get(j));
				for(int i = 0; i < I; i++){
					if(Y.get(j).get(i)==1){
						Xtemp.add(siteX.get(i));
						Ytemp.add(siteY.get(i));
					}
				}
				coorX.add(Xtemp);
				coorY.add(Ytemp);
			}
			for(int i = 0; i < coorX.size(); i++){
				for(int j = 0; j < coorX.get(i).size(); j++){
					output2.print(coorX.get(i).get(j) + " ");
				}
				output2.println();
				for(int j = 0; j < coorY.get(i).size(); j++){
					output2.print(coorY.get(i).get(j) + " ");
				}
				output2.println();
			}
			output.println("solution Y:");
			for(int j=0; j<J; j++){
				output.println(Y.get(j));
			}
			double cost = 0;
			for(int j = 0; j < J; j++){
				cost = cost + f_j.get(j)* (double)X.get(j);
				for(int i=0; i<I; i++){
					cost = cost + (double)Y.get(j).get(i) * h_i.get(i) * d_ji.get(j).get(i);
				}
			}
			output.println("cost:");
			output.println(cost);
			GAP.add( 100.0 * ((double)cost-lowerbound.get(ncount)) / (double)cost);
			output.println("solution X:");
			output.println(X);
		}
		output.println("lower bound:");
		output.println(lowerbound);
		output.println("GAP:");
		output.println(GAP);
		output2.close();
		output.close();
		scanner.close();
	}

	private static List<Integer> SolveDP(int I,int J,int j,List<Integer> h_i,int K,double f,List<Double> lambda_i,List<List<Double>> d_ji,double[] L){
		int demand = 0;
		for(int i =0; i< I; i++){
			demand += h_i.get(i);
		}
		if(demand <= K){
			List<Integer> temp_Y = new ArrayList<Integer>();
			for(int i=0; i<I; i++){
				if((h_i.get(i) * d_ji.get(j).get(i) - lambda_i.get(i)) < 0){
					temp_Y.add(1);
				}
				else{
					temp_Y.add(0);
				}
			}
			return temp_Y;
		}
		List<List<Double>> dp_ik = new ArrayList<List<Double>>();
		//initial value if f_j * X_j(which is 1)
		dp_ik.add(new ArrayList<Double>(Collections.nCopies(K+1, (Double)0.0)));
		//bottom up dp table
		for(int i=0;i<I;i++){
			List<Double> dp_i = new ArrayList<Double>();
			int h = h_i.get(i);
			double temp = h * d_ji.get(j).get(i) - lambda_i.get(i);
			if(temp >=0){
				//skip i
				dp_ik.add(new ArrayList<Double>(Collections.nCopies(K+1, (Double)0.0)));
			}
			else{
				for(int k=0;k<=K;k++){
					if(h > k){
						//System.out.print("if  : i "+i+" j "+j+" k "+k+" h "+h+"\n");
						dp_i.add(dp_ik.get(i).get(k));
					}
					else{
						//System.out.print("else: i "+i+" j "+j+" k "+k+" h "+h+"\n");
						if( (dp_ik.get(i).get(k)) < (temp + dp_ik.get(i).get(k-h))  ){
							dp_i.add(dp_ik.get(i).get(k));
						}
						else{
							dp_i.add(temp + dp_ik.get(i).get(k-h));
						}
					}
				}
				dp_ik.add(dp_i);
			}
		}
		if((dp_ik.get(I).get(K)+f) < 0) {
			L[0] = L[0] + dp_ik.get(I).get(K) + f;
			System.out.println("printL: " + L);
			//X_j =1
			//dp_ik.at(I+1).at(K) is the optimal solution and we want to backtrack Y_ji of j
			ArrayList<Integer> Y_i = new ArrayList<Integer>(Collections.nCopies(I, 0));
			int cur = I;
			while(K>0 && cur>0){
				if(dp_ik.get(cur).get(K) == dp_ik.get(cur-1).get(K)){
					//do not choose cur
					cur--;
				}
				else{
					Y_i.set(cur-1,1);
					K = K - h_i.get(cur-1);
					cur--;
				}
			}
			//adding jth term of L(lambda)
			return Y_i;
		}
		else{
			//X_j = 0
			return (new ArrayList<Integer>(Collections.nCopies(I, 0)));
		}
	}
	
	private static Map<String, List<List<Integer>>> CAPBACKUP(int I, int J, List<Integer> h, List<List<Double>> d, List<Double> f,List<Integer> k, List<List<Integer>> Y_bar, List<Integer> X_bar){
		for(int j = 0; j < J; j++){
	    	System.out.print(Y_bar.get(j) + " \n");
	    }
	    System.out.print(" \n" + X_bar + " \n");
		//step 1
	    double demand = 0;
	    double capacity = 0;
	    for(int i = 0; i < I ; i++){
	        demand += h.get(i);
	    }
	    List<Double> Sj = new ArrayList<Double>();
	    List<Integer> Sj_index = new ArrayList<Integer>();
	    for(int j=0; j<J; j++){
	        if(X_bar.get(j) == 0){
	            Sj.add(f.get(j));
	            Sj_index.add(j);
	        }
	    }
	    for(int i = 0; i < J ; i++){
	        capacity = capacity + (k.get(i) * X_bar.get(i));
	    }
	    while(capacity < demand){
	    	int indexInSj = Sj.indexOf(Collections.min(Sj));
	        int index = Sj_index.get(indexInSj);
	        X_bar.set(index, 1);
	        capacity = capacity + k.get(index);
	        Sj.remove(indexInSj);
	        Sj_index.remove(indexInSj);
	    }
	    
	    //step 2,3
	    List<Integer> Si = new ArrayList<Integer>();
	    for(int i=0; i<I; i++){
	        Si.add(i);
	    }
	    for(int i=0; i<I; i++){
	        int Ycount = 0;
	        for(int j=0; j<J; j++){
	            List<Integer> Yj = Y_bar.get(j);
	            Ycount = Ycount + Yj.get(i);
	        }
	        if(Ycount == 1){
	            Si.remove(Si.indexOf(i));
	        }
	        else if(Ycount > 1){
	            int c = 0, minj = 0; 
	            double	min = 0.0;
	            for(int j = 0; j < J; j++){
	                List<Integer> Yj = Y_bar.get(j);
	                int Yij = Yj.get(i);
	                if(Yij == 1){
	                    Yj.set(i, 0);
	                    Y_bar.set(j, Yj);
	                    List<Double> dj = d.get(j);
	                    double dij = dj.get(i);
	                    if(dij < min ){
	                        min = dij;
	                        minj = j;
	                    }
	                    else if(c == 0){
	                        min = dij;
	                        minj = j;
	                        c++;
	                    }
	                }
	            }
	            List<Integer> Yj = Y_bar.get(minj);
	            Yj.set(i, 1);
	            Y_bar.set(minj, Yj);
	            Si.remove(Si.indexOf(i));
	        }
	    }
	//step 4
	    List<List<Double>> dij = new ArrayList<List<Double>>();
	    List<List<Integer>> dijIndex = new ArrayList<List<Integer>>();
	    List<Double> currentCap = new ArrayList<Double>();
	    for(int i=0; i<Si.size(); i++){
        	int index = Si.get(i);
        	List<Double> di = new ArrayList<Double>();
        	List<Integer> diIndex = new ArrayList<Integer>();
        	for(int j =0; j < J; j++){
        		List<Double> temp = d.get(j);
        		if( X_bar.get(j) == 1){
        			di.add(temp.get(index));
        			diIndex.add(j);
        		}
        	}
        	dij.add(di);
        	dijIndex.add(diIndex);
        }
	    for(int j = 0; j < J; j++){
	    	List<Integer> Yj = Y_bar.get(j);
	    	double Capa = 0;
	    	for(int i = 0; i < I; i++){
	    		Capa = Capa + Yj.get(i) * h.get(i);
	    	}
	    	currentCap.add(Capa);
	    }
	    while(Si.size() != 0){
	    	List<Double> penalty = new ArrayList<Double>();
	    	List<Double> minj = new ArrayList<Double>();
	    	List<Integer> minIndexj = new ArrayList<Integer>();
	        for(int i = 0; i < Si.size(); i++){
	        	List<Double> di = dij.get(i);
	        	List<Integer> diIndex = dijIndex.get(i);
	        	while(true){
	        		double min = Collections.min(di);
	        		int minIndex = di.indexOf(min);
	        		if(currentCap.get(diIndex.get(minIndex)) + h.get(Si.get(i)) <= k.get(diIndex.get(minIndex))){
	        			List<Double> temp = new ArrayList<Double>(di);
	        			temp.remove(minIndex);
	        			if(temp.size() != 0){
	        				penalty.add(Collections.min(temp)- min );
	        			}
	        			else{
	        				penalty.add(5000.0);
	        			}
	        			minj.add(min);
	        			minIndexj.add(diIndex.get(minIndex));
	        			break;
	        		}
	        		else{
	        			di.remove(minIndex);
	        			diIndex.remove(minIndex);
	        		}
	        	}
	        	dij.set(i, di);
	        	dijIndex.set(i, diIndex);
	        }
	        int indexSi = penalty.indexOf(Collections.max(penalty));
	        int indexI = Si.get(indexSi);
	        int indexJ = minIndexj.get(indexSi);
	        List<Integer> Yj = Y_bar.get(indexJ);
	        Yj.set(indexI, 1);
	        Y_bar.set(indexJ, Yj);
	        Si.remove(indexSi);
	        currentCap.set(indexJ, currentCap.get(indexJ) + h.get(indexI));
	    }
	    Map<String, List<List<Integer>>> returnMap = new HashMap<String, List<List<Integer>>>();
	    returnMap.put("Y", Y_bar);
	    List<List<Integer>> X_temp = new ArrayList<List<Integer>>();
	    X_temp.add(X_bar);
	    returnMap.put("X", X_temp);	
	    /*for(int j = 0; j < J; j++){
	    	System.out.print(Y_bar.get(j) + " \n");
	    }
	    System.out.print(" \n" + X_bar + " \n");*/
	    return returnMap;
	}

}