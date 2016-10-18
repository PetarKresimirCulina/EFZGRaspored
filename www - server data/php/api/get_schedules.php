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
				// day | units in day | duration | roomid | period | group id | course name | execution type | tutor name | tutor last name | tutor code | room name
				echo $row[4] . '&' . $row[5] . '&' . $row[6] . '&' . $row[7] . '&' . $row[8] . '&' . $row[9] . '&' . $row[13] . '&' . $row[15] . '&' . $row[22] . '&' . $row[23] . '&' . $row[24] . '&' . $row[26] . '<>';
			}
		}
		else { return 0; }
	}
?>