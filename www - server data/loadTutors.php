<?php
	function loadTutors($xml, $connection)
	{
		echo "\n\r" . 'Loading Tutors';
		$connection->query("TRUNCATE TABLE tutors");
		foreach($xml->tutors->tutor as $item)
		{
			$id = $connection->real_escape_string($item->attributes()->id);
			$name = $connection->real_escape_string($item->attributes()->name);
			$surname = $connection->real_escape_string($item->attributes()->surname);
			//$code = $connection->real_escape_string($item->attributes()->code);
			
			if(isset($item->attributes()->code))
			{
				$code = $connection->real_escape_string($item->attributes()->code);
			}
			else
			{
				$code = 'none';
			}
			
			if(!mysqli_query($connection, "INSERT INTO `tutors` (id, `name`, `surname`, `code`) VALUES ($id, '$name', '$surname', '$code');"))
			{
				echo mysqli_error($connection);
			}
		}
	}
?>