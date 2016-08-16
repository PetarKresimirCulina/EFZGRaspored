<?php
	// Fetching results from MySQL for Android app
	// (c) 2016 Petar-Kresimir Culina
	include('../connection_db.php');
	
	//$method = intval($_POST['method']);
	
	$connection = connectDB();
	getPrograms($connection);
	
	$connection->close();
	
	function getPrograms ($connection) //method 0
	{
		$result = $connection->query("SELECT * FROM programs ORDER BY  `id` ASC ");
		if($result->num_rows > 0)
		{
			while($row = mysqli_fetch_array($result))
			{
				$response = $row['id'] . '&' . $row['name'] . '&' . $row['years'] . '<>';
				echo $response;
			}
		}
		else { return 0; }
	}
?>