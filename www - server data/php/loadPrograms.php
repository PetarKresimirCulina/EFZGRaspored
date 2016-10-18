<?php
	function loadPrograms($xml, $connection)
	{
		echo "\n\r" . 'Loading Programs';
		$connection->query("TRUNCATE TABLE programs");
		foreach($xml->programs->program as $item)
		{
			$id = $connection->real_escape_string($item->attributes()->id);
			$name = $connection->real_escape_string($item->attributes()->name);
			$years = $connection->real_escape_string($item->attributes()->years);
			
			if(!mysqli_query($connection, "INSERT INTO `programs` (id, `name`, years) VALUES ($id, '$name', $years);"))
			{
				echo mysqli_error($connection);
			}
		}
	}
?>