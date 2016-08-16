<?php
	function loadCourses($xml, $connection)
	{
		echo "\n\r" . 'Loading Courses';
		$connection->query("TRUNCATE TABLE courses");
		foreach($xml->courses->course as $item)
		{
			$course_id = $connection->real_escape_string($item->attributes()->id);
			$name = $connection->real_escape_string($item->attributes()->name);
			$branch_id = $connection->real_escape_string($item->attributes()->branches);
			
			if(strpos($branch_id, ',') !== false)
			{
				$arr = explode(',', $branch_id, 2);
				$branch_id = $arr[0];
			}
			
			foreach($item->course_part as $sub_item)
			{
				$execution_type = $connection->real_escape_string($sub_item->attributes()->type);
				
				foreach($sub_item->turn as $turn)
				{
					$turn_id = $connection->real_escape_string($turn->attributes()->id);
					$tutors = $connection->real_escape_string($turn->attributes()->tutors);
					
					if(strpos($tutors, ',') !== false)
					{
						$arr = explode(',', $tutors, 2);
						$tutors = $arr[0];
					}
					
					if($tutors == ''){ $tutors = 0; }
					
					foreach($turn->turn_part as $turn_part)
					{
						$turn_part_id = $connection->real_escape_string($turn_part->attributes()->id);
						$groups = $connection->real_escape_string($turn_part->attributes()->groups);
						
						echo $groups . "</br>";
						if(strpos($groups, ',') !== false && $groups != '')
						{
							$arr = explode(',', $groups);
							
							foreach($arr as $group)
							{
								$result = $connection->query("SELECT parent_id FROM groups WHERE id=$group");
								$parent_id_arr = mysqli_fetch_array($result);
								$parent = $parent_id_arr[0];
								
								if(!mysqli_query($connection, "INSERT INTO `courses` (turn_part_id, `name`, branch_id, execution_type, turn_id, tutors, course_id, groups, groups_parent) VALUES ($turn_part_id, '$name', $branch_id, $execution_type, $turn_id, $tutors, $course_id, $group, $parent);"))
								{
									echo "\n\r" . mysqli_error($connection)  . "\n\r part id " . $turn_part_id;
									die;
								}
							}
						}
						else if ($groups != '')
						{
							$result = $connection->query("SELECT parent_id FROM groups WHERE id=$groups");
							$parent_id_arr = mysqli_fetch_array($result);
							$parent = $parent_id_arr[0];
							
							if(!mysqli_query($connection, "INSERT INTO `courses` (turn_part_id, `name`, branch_id, execution_type, turn_id, tutors, course_id, groups, groups_parent) VALUES ($turn_part_id, '$name', $branch_id, $execution_type, $turn_id, $tutors, $course_id, $groups, $parent);"))
							{
								echo "\n\r" . mysqli_error($connection)  . "\n\r part id " . $turn_part_id;
								die;
							}
						}
					}
				}
			}
		}
	}
?>