#!/bin/bash
# enable version control in /about.html
echo -e "<html>  
   	<head>   
       	<title>GE APM</title>  
       	<meta charset="utf-8">  
   	</head>  
   	<body>  
		<div>GE Healthcare (China) APM</div>
		<div>Version: \$version</div>
		<div>Commit: \$commit</div>  
   	</body>  
</html>" > ./src/main/webapp/about.html

branch=`git branch |grep \* |awk '{print $2}'`

if [ "$branch" = "develop" ]; then
    version=2.0
elif [ "$branch" = "master" ]; then
    version=1.0
else 
    version=1.2
fi

commit=`git log -1 |grep commit |awk '{print $2}'`

echo "version: $version"
echo "commit: $commit"
echo "branch: $branch"


echo `sed  's/$version/'"$version"'/'  ./src/main/webapp/about.html` > ./src/main/webapp/about.html
echo `sed  's/$commit/'"$commit"'/' ./src/main/webapp/about.html` > ./src/main/webapp/about.html

