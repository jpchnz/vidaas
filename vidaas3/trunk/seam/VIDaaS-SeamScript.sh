echo $1
if [ $# -gt 0 ]; then
	if [ -d $1 ]; then
		cd $1
		if [ -f $1/seam ]; then
			echo "Generating New Project"
			`pwd`/seam new-project

			sleep 2
			echo "Generating Entities"
			`pwd`/seam generate-entities

			sleep 2
			echo "Deploying Web Application"
			`pwd`/seam explode
		else 
			echo "Seam script not found in the directory"
		fi
	else 
		echo "Command Line argument is not directory"
	fi
else 
	echo "Command Line Argument is missing"
fi
