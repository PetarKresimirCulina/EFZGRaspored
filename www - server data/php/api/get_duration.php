<?php
	include('../connection_db.php');
	$connection = connectDB();
	
	getDuration($connection);

	$connection->close();

	function getDuration ($connection)
	{
		$result = $connection->query("SELECT * FROM duration");
		if($result->num_rows > 0)
		{
			while($row = mysqli_fetch_array($result))
			{	
				echo $row[1] . '&' . $row[2];
			}
		}
		else { return 0; }
	}
?>