<?php
	function loadDuration($start, $end, $connection)
	{
		echo "\n\r" . 'Loading Duration';
		$connection->query("TRUNCATE TABLE duration");
		
		
		if(!mysqli_query($connection, "INSERT INTO `duration` (start, end) VALUES ($start, $end);"))
		{
			echo mysqli_error($connection);
		}
	}
?>