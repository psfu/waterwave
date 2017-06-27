# waterwave

### About

* a framework，which implements of the modle of AIO BIO NIO and which with optimization of it's buffer.
* an implementation of proxy which is using this framework.
* it is just depends on the JDK lib, witch with out other libs.

* 一个网络框架，实现主流的AIO NIO BIO等模型，以及相应的缓存优化
* 实现了一版基于框架的 PROXY 代理功能
* 框架不依赖JDK外的任何外部LIB

### TODO
* protocols interface  
* implementations of parser for HTTP protocols and MYSQL protocols
* implementations of recording for the Proxy's logging

* 下一步增加协议接口
* 实现HTTP,MYSQL协议解析
* 代理的日志接口，和实现


### First test （proxy）:

#### ENV
* VM: Xeon E312xx 2.4Ghz X 8 16GB RAM
* MYSQL 5.7.17
* JAVA 1.8.74
* NET VM NETWORK, SAME SEGMENT

#### DESC
* AIO:     
  > 2 thread for R/W handler of server and also for handler of working,
  > 2 thread for R/W handler of client and also for handler of working 
* NIO: 
  > 1 thread for R/W handler of server, 
  > 1 thread for R/W handler of client, 
  > n hread for handler of working
* BIO: 
  > n thread for R/W handler of server and R/W handler of client and also for handler of working
* NIO(1t): 
  > 1 thread for R/W handler of server and R/W handler of client and also for handler of working
* NETTY(1t): 
  > 1 thread for R/W handler of server and R/W handler of client and also for handler of working
  
#### RESULT
* just directly connecting to Mysql can upto 100K QPS 
* the best result is the single thread Proxy(NIO), and it may reach 75K QPS, wich is with low CPU using.
* proxy using BIO may reach 70K QPS , wich is with some CPU using.
* proxy using AIO may reach 70K QPS , wich is with some higher CPU using.
* due to using synchronization in the implementation of the writer, the NIO with 1T + 1T + nT implementation has low QPS, this may improve by changing the strategy.    

#### THREADS QPS (KQPS)					

| T   	| DB QPS（NOPROXY）	| AIO(2T+2T) 	| NIO(1T+1T+nT) 	| BIO(nT) 	| NIO(1T) 	| NETTY(1T) 	|
|-----	|---------			|------------	|---------------	|---------	|---------	|-----------	|
| 20  	| 50      			| 30         	| 15            	| 32      	| 35      	| 30        	|
| 40  	| 90      			| 52         	| 18            	| 50      	| 59      	| 46        	|
| 60  	| 100     			| 62         	| 21            	| 65      	| 63      	| 60        	|
| 80  	| 105     			| 62         	| 19            	| 68      	| 72      	| 67        	|
| 100 	| 105     			| 68         	| 19            	| 72      	| 74      	| 74        	|					
						
#### THREADS CPU USING (%)						

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


 

