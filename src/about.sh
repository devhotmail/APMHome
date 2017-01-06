version=1.0
build=`svn log --limit 1 |awk 'NR==2{print}' |awk '{print $1}'`
build=${build#*r}

sed -i 's/$version/'"$version"'/' ./target/geapm-*/about.html 
sed -i 's/$build/'"$build"'/' ./target/geapm-*/about.html




