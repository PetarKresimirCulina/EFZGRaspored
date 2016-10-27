<?php
	include('../connection_db.php');
	$connection = connectDB();

	$groups = intval($_POST['groupid']);
	
	getGroups($connection, $groups);

	$connection->close();

	function getGroups ($connection, $groups)
	{
		$result = $connection->query("SELECT * FROM schedules, courses, tutors, rooms WHERE schedules.turn_part_id=courses.turn_part_id AND schedules.parent_id = $groups AND courses.groups_parent = $groups AND courses.tutors = tutors.id AND schedules.room_id=rooms.id");
		if($result->num_rows > 0)
		{
			while($row = mysqli_fetch_array($result))
			{
				$parent_id = $row[10];
				$group_id = $row[9];
				$group_name = "null";
				
				// Provjeri ako je grupa parent ili child, ako je child onda echoaj isti odg kao i da nije samo uvrsti variablu iza sa nazivo podgrupe
				if ($group_id != $parent_id)
				{
					$result_gn = $connection->query("SELECT * FROM groups WHERE id=$group_id");
					$gn = mysqli_fetch_array($result_gn);
					$group_name = $gn[1];
				}
				
				// day | units in day | duration | roomid | period | group id | course name | execution type | tutor name | tutor last name | tutor code | room name
				echo $row[4] . '&' . $row[5] . '&' . $row[6] . '&' . $row[7] . '&' . $row[8] . '&' . $row[9] . '&' . $row[13] . '&' . $row[15] . '&' . $row[22] . '&' . $row[23] . '&' . $row[24] . '&' . $row[26] . '&' . $group_name . '<>';
			}
		}
		else { return 0; }
	}
?>