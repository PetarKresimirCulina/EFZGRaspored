<?php
	function loadExecutionTypes($xml, $connection)
	{
		echo "\n\r" . 'Loading ExecutionTypes';
		$connection->query("TRUNCATE TABLE execution_types");
		foreach($xml->execution_types->type as $item)
		{
			$id = $connection->real_escape_string($item->attributes()->id);
			$name = $connection->real_escape_string($item->attributes()->name);
			
			if(!mysqli_query($connection, "INSERT INTO `execution_types` (id, `name`) VALUES ($id, '$name');"))
			{
				echo mysqli_error($connection);
			}
		}
	}
?>