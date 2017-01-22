# waterwave

## first test:

#### ENV
* VM: Xeon E312xx 2.4Ghz X 8 16GB RAM
* MYSQL 5.7.17
* JAVA 1.8.74
* NET VM NETWORK, SAME SEGMENT

#### DESC
* AIO: 2 thread for server R/W handler, 2 thread for client R/W handler 
* NIO: 1 thread for server R/W handler, 1 thread for client R/W handler, nT hread for work handler
* BIO: n thread for server R/W client R/W and work handler
* NIO(1t): 1 thread for server R/W client R/W and work handler
* NETTY(1t): 1 thread for server R/W client R/W and work handler




#### THREADS QPS					

| T   	| DB QPS（NOPROXY）	| AIO(2T+2T) 	| NIO(1T+1T+nT) 	| BIO(nT) 	| NIO(1T) 	| NETTY(1T) 	|
|-----	|---------			|------------	|---------------	|---------	|---------	|-----------	|
| 20  	| 50      			| 30         	| 15            	| 32      	| 35      	| 30        	|
| 40  	| 90      			| 52         	| 18            	| 50      	| 59      	| 46        	|
| 60  	| 100     			| 62         	| 21            	| 65      	| 63      	| 60        	|
| 80  	| 105     			| 62         	| 19            	| 68      	| 72      	| 67        	|
| 100 	| 105     			| 68         	| 19            	| 72      	| 74      	| 74        	|					
						
#### THREADS CPU USING						

| T   	| DB CPU（NOPROXY）	| AIO(2T+2T) 	| NIO(1T+1T+nT) 	| BIO(nT) 	| NIO(1T) 	| NETTY(1T) 	|
|-----	|---------			|------------	|---------------	|---------	|---------	|-----------	|
| 20  	| 400     			| 250        	| 200           	| 140     	| 70      	| 100       	|
| 40  	| 700     			| 300        	| 250           	| 290     	| 89      	| 150       	|
| 60  	| 780     			| 310        	| 270           	| 320     	| 90      	| 220       	|
| 80  	| 790     			| 310        	| 260           	| 380     	| 92      	| 240       	|
| 100 	| 790     			| 320        	| 260           	| 420     	| 93      	| 220       	|


#### THREADS QPS
 ![WW-QPS-201701.png](https://github.com/psfu/waterwave/raw/master/report/WW-QPS-201701.png)
 
#### THREADS CPU USING
 ![WW-CPU-201701.png](https://github.com/psfu/waterwave/raw/master/report/WW-CPU-201701.png)


 

