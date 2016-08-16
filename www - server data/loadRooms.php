<?php
	function loadRooms($xml, $connection)
	{
		echo "\n\r" . 'Loading Rooms';
		$connection->query("TRUNCATE TABLE rooms");
		foreach($xml->rooms->room as $item)
		{
			$id = $connection->real_escape_string($item->attributes()->id);
			$name = $connection->real_escape_string($item->attributes()->name);
			$num_of_seats = $connection->real_escape_string($item->attributes()->num_of_seats);
			$room_properties = $connection->real_escape_string($item->attributes()->room_properties);
			
			if($room_properties == '') { $room_properties = 0;}
			
			if(!mysqli_query($connection, "INSERT INTO `rooms` (id, `name`, num_of_seats, room_properties) VALUES ($id, '$name', $num_of_seats, $room_properties);"))
			{
				echo mysqli_error($connection);
			}
		}
	}
?>