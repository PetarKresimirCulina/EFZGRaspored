<?php
	function loadTimeSlots($xml, $connection)
	{
		$db = $_SESSION['database'];
		echo "\n\r" . 'Loading Timeslots';
		$connection->query("TRUNCATE TABLE timeslots");
		foreach($xml->timeslots->slot as $item)
		{
			$id = $connection->real_escape_string($item->attributes()->id);
			$from = $connection->real_escape_string($item->attributes()->from);
			$to = $connection->real_escape_string($item->attributes()->to);
			
			if(!mysqli_query($connection, "INSERT INTO `$db`.`timeslots` (id, `from`, `to`) VALUES ($id, '$from', '$to');"))
			{
				echo mysqli_error($connection);
			}
		}
	}
?>