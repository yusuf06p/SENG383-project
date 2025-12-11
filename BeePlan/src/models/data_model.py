from dataclasses import dataclass, field
from typing import List, Optional

# Global Sabitler
DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]

@dataclass
class TimeSlot:
    day: str
    hour: int  # 9, 10, ... 16

    def __hash__(self):
        return hash((self.day, self.hour))
    def __eq__(self, other):
        return isinstance(other, TimeSlot) and self.day == other.day and self.hour == other.hour

@dataclass
class Course:
    code: str
    name: str
    theoretical_hours: int
    lab_hours: int
    total_hours: int
    year: int
    is_elective: bool = False
    instructor_id: Optional[str] = None
    capacity: int = 0
    section_count: int = 1

@dataclass
class Instructor:
    id: str
    name: str
    max_daily_theory: int = 4
    availability: List[TimeSlot] = field(default_factory=list)

@dataclass
class ScheduleEntry:
    course: Course
    time_slot: TimeSlot
    room_id: str
    section: int
    is_lab: bool