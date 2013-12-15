- - - - - - - - - - - - - - - - - - - - - - - -  config elasticsearch enviroment: - - - - - - - - - - - - - - - - - - - - - - - -  
use this official guide:
http://www.elasticsearch.org/guide/
(I strongly recommend the "head" plugin! http://www.elasticsearch.org/guide/reference/modules/plugins/)

To discover other nodes, you should modify the "elasticsearch.yml" under PATH_elasticsearch/config.





- - - - - - - - - - - - - - - - - - - - - - - - - - - prepare for the search: - - - - - - - - - - - - - - - - - - - - - - - - - -
1. modify the "desk.properties" under ElasticSearch/data/:

	index_name = desks
	master_ip = 166.111.69.204
	distanceBound = 500000

	The "index_name" is the name of index in elasticsearch, and "master_ip" refers to the ip address of one node in elastic_search. 
	The distanceBound means the tolerance of distance from user to the fatherest topK POI. (500000 ≈ 50km)

2. modify the "poi.csv" under ElasticSearch/data/:
	
	This is the main data, and you should format the data like this: 
	
	id  latitude	longitude	name
	1	44.968731	-89.636891	Northway Communications
	3	46.715251	-122.954566	Washington State Driving School
	5	34.004789	-117.328692	Icbm
	6	41.66791	-71.53612	Conimict Village Pizza
	7	29.743723	-94.96186	L and L Welding and Remodeling

	Each line refers to a poi including id, latitude, longitude, name. Each parameter is separated '\t'.

3. run "setup.sh" under ElasticSearch/:
	
	./setup.sh

	After this step, you have done these works: 
	a. get the Mercator value of the poi
	b. build the index
	c. upload the data and index

	And you will find some new files under "ElasticSearch/data/". This step will take a relatively long time.(^_^)

4. add queries:
	
	modify the "qeury.txt" under ElasticSearch/data/:

	mcdonald
	2 34.026238 -118.4315282106 120 270
	coffee starbucks
	2 34.026238 -118.4315282106 20 170
	supermarket food
	3 34.026238 -118.4315282106 20 300
	...

	namelist
	topK latitude longitude degree_start degree_end

	Each query includes two lines: the first line refers to the name of the keywords which is separated by ' ', the second line 
	includes the following parameters: topK, latitude, longitude, degree_start, degree_end.

5. run "query.sh" under ElasticSearch/:
	
	./query_fast.sh
	./query_normal.sh

	query_fast.sh: our direction-aware search algorithm
	query_normal.sh: normal search in elasticsearch

	Now you can get all the results!




