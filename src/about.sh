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
		<div>Build: \$build</div>  
   	</body>  
</html>" > ./src/main/webapp/about.html

version=1.0
build=`svn log --limit 1 |awk 'NR==2{print}' |awk '{print $1}'`
build=${build#*r}

echo "Version: $version"
echo "Build: $build"

sed -i 's/$version/'"$version"'/'  ./src/main/webapp/about.html
sed -i 's/$build/'"$build"'/' ./src/main/webapp/about.html

