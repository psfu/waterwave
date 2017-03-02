# waterwave

## About

* a network framework，implements modes of AIO BIO NIO and buffer optimization
* an implementation of proxy based this network framework
* may no additional lib is in dependency
* -
* 一个网络框架，实现主流的模型AIO NIO BIO等，以及相应的缓存优化
* 实现了一版基于框架的 PROXY 代理功能
* 框架计划一直不依赖JDK外任何外部LIB

## TODO
* protocols interface  
* HTTP protocols, MYSQL protocols analysis implements of protocols interface
* proxy data log interface and implementations
* -
* 下一步增加协议接口
* 基于定义的接口实现HTTP,MYSQL协议解析
* 代理的数据日志接口，以及实现


## First test （proxy）:

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

#### RESULT
* just Mysql can be 100K QPS 
* the best is the single thread Proxy, and may reach 75K QPS with low CPU using
* with the handler it may reach 70K QPS with some CPU using
* due to the implementation with synchronization in the writer, the NIO with 1T + 1T + nT implementation has low QPS, this may improve by the changes of the strategy    

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


 

