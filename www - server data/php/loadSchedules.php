<?php
	function loadSchedules($xml, $connection)
	{
		echo "\n\r" . 'Loading Schedules';
		$connection->query("TRUNCATE TABLE schedules");
		foreach($xml->schedules->schedule as $item)
		{
			$schedule_id = intval($connection->real_escape_string($item->attributes()->id));
			$course_id = intval($connection->real_escape_string($item->attributes()->course_id));
			$turn_part_id = intval($connection->real_escape_string($item->attributes()->turn_part_id));
			$day = intval($connection->real_escape_string($item->attributes()->day));
			$units_in_day = intval($connection->real_escape_string($item->attributes()->units_in_day));
			$duration = intval($connection->real_escape_string($item->attributes()->duration));
			$room_id = intval($connection->real_escape_string($item->attributes()->room_id));
			$period = $connection->real_escape_string($item->attributes()->period);
			
			//Saznaj za koju se grupu odnosi preko turn_part_id - lakše je tako nego da mobitel to radi, brže će se app izvoditi
			
			$result = $connection->query("SELECT * FROM courses WHERE turn_part_id=$turn_part_id");
			while($row = mysqli_fetch_array($result))
			{
				$group = $row['groups'];
				$group = intval($group);
				
				// Saznaj koji je parent_id grupe, lakše će se loadati raspored ako znamo matičnu grupu
				$result = $connection->query("SELECT parent_id FROM groups WHERE id=$group");
				$parent_id_arr = mysqli_fetch_array($result);
				$parent = $parent_id_arr[0];
				
				if(!mysqli_query($connection, "INSERT INTO `schedules` (schedule_id, course_id, turn_part_id, day, units_in_day, duration, room_id, period, group_id, parent_id) VALUES ($schedule_id, $course_id, $turn_part_id, $day, $units_in_day, $duration, $room_id, '$period', $group, $parent);"))
				{
					echo mysqli_error($connection);
				}
			}
		}
	}
?>