# What versions are these web services running?

What-runs-where is a service that parses and aggregates the version strings of any number of web services which choose to publish their version. 
It is useful when you have several testing&prod environments where several versions of your web services are running and you want to have a quick 
overview of what runs where.<br/>
Check-out <a href="http://vast-brook-8589.herokuapp.com/">this live example</a> running on Heroku (see also the Heroku section way below). 

## Building

Install Lein from http://leiningen.org<br/>
Build and package the binary with:

<pre>
$ lein clean
$ lein uberjar
</pre>

##Running
WrW is itself a web service. It publishes its version aggregations as a web service presented both as a rich HTML page or a JSON API. To start the server: 

<pre>
$ java -jar what-runs-where-0.1.0-SNAPSHOT-standalone.jar -c test/what_runs_where/test/config-test.properties
</pre>

This command starts a Jetty webserver that runs on localhost at port 8000. Use http://localhost:8000 to reach the service webpage. Use http://localhost:8000/api/services for the JSON API.<br/>
There are command-line options too:
<pre>
 Switches  Default            Desc               
 --------  -------            ----               
 -c        config.properties  Configuration file 
 -p        8000               Port               
</pre>
The default config.properties is not supplied, you'll have to build it as described below.

## Configuration and examples
WrW configures itself from a properties file where services are defined. The services are grouped conveniently behind labels allowing clustering of environments e.g. staging and production. 

Note the inherent constraints of the Java Properties file.
<pre>  
Cool\ Service= {
    :servers ["http://stackoverflow.com"], 
    :parsers [
		{
			:path "/",
			:regexp #"&lt;div id=\\"svnrev\\">\\s*(.*)\\s*&lt;/div&gt;", 
			:name "Stackoverflow"
		}
	]
}
</pre>
Cool\ Service is the group label
:servers defines a list of services for which a version can be extracted.<br/>
:parsers is a list of version parsers since a web service may publish more than its own version, possibly showing versions of submodules and/or dependent services.<br/>
:path is the path to the page whose body contains the version string<br/>
:regexp the regex to parse the said version string<br/>
:name the human readable name of the service.

The configuration needs to be inlined in the properties file. See <https://github.com/florind/what-runs-where/blob/master/test/what_runs_where/test/config-test.properties> for how inlining works with the above example. 

Here's a second example: WrW itself publishes its own version along with the Clojure version that powers it. The version URL is http://localhost:8000/version and the response body is:
<pre>
{
	"version": "0.1.0", 
	"clojure": "1.5.1"
}
</pre>

In order to parse both versions and show them on the WrW dashboard we'll use the following configuration:
<pre>
What\ Runs\ Where\ Local= {
	:servers ["http://localhost:8000"], 
	:parsers [
		{
			:path "/version", 
			:regexp #"\\"version\\": \\"([\\w\\d.]*)", 
			:name "WrW"
		}
		{
			:path "/version", 
			:regexp #"\\"clojure\\": \\"([\\w\\d.]*)", 
			:name "Clojure"
		}
	]
}
</pre>
Notice the two parsers that are applied against the same /version path and which pick apart the WrW and clojure versions respectively.<br/>
Again, this configuration is inlined in the same <https://github.com/florind/what-runs-where/blob/master/test/what_runs_where/test/config-test.properties> example.


##JSON API
WrW also publishes a JSON API, reachable under the path /api/services. Example:
<pre>
[
	{
		http://localhost:8000: {
			Clojure: "1.5.1",
			WrW: "0.1.0"
		}
	},
		{
			http://stackoverflow.com: {
			Stackoverflow: "rev 2013.5.24.702"
		}
	}
]
</pre>

##Heroku
WrW is Heroku-friendly too. The instance config file is https://github.com/florind/what-runs-where/blob/master/config-heroku.properties 
and runs <a href="http://vast-brook-8589.herokuapp.com/">here</a>. Note that the WrW server DNS is hard-coded in the config file and alas, not dynamically compiled.

## Built with

[<img src="http://clojure.org/space/showimage/clojure-icon.gif">](http://clojure.org) [<img src="http://twitter.github.com/bootstrap/assets/img/bs-docs-responsive-illustrations.png">] (http://twitter.github.com/bootstrap/)

## License

WrW is distributed under the terms of the MIT License.
