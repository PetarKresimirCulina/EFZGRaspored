<?php
	function loadBranches($xml, $connection)
	{
		echo "\n\r" . 'Loading Branches';
		$connection->query("TRUNCATE TABLE branches");
		foreach($xml->branches->branch as $item)
		{
			$id = $connection->real_escape_string($item->attributes()->id);
			$name = $connection->real_escape_string($item->attributes()->name);
			$code = $connection->real_escape_string($item->attributes()->code);
			$year = $connection->real_escape_string($item->attributes()->year);
			$program_id = $connection->real_escape_string($item->attributes()->program_id);
			
			if(!mysqli_query($connection, "INSERT INTO `branches` (id, `name`, code, year, program_id) VALUES ($id, '$name', $code, $year, $program_id);"))
			{
				echo mysqli_error($connection);
			}
		}
	}
?>