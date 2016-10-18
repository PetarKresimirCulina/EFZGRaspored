<?php
	function loadRoomProperties($xml, $connection)
	{
		echo "\n\r" . 'Loading RoomProperties';
		$connection->query("TRUNCATE TABLE room_properties");
		foreach($xml->room_properties->property as $item)
		{
			$id = $connection->real_escape_string($item->attributes()->id);
			$name = $connection->real_escape_string($item->attributes()->name);
			
			if(!mysqli_query($connection, "INSERT INTO `room_properties` (id, `name`) VALUES ($id, '$name');"))
			{
				echo mysqli_error($connection);
			}
		}
	}
?>