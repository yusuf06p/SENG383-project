import csv
import re
import os
from typing import Dict, List, Tuple
# Import yoluna dikkat:
from src.models.data_model import Course, TimeSlot, Instructor, ScheduleEntry, DAYS


def parse_hours(hour_string: str) -> Tuple[int, int]:
    """Örn: '3+2' -> (3, 2)"""
    if "+" in hour_string:
        parts = hour_string.split('+')
        if '(' in parts[0]:
            parts[0] = parts[0].split('(')[-1]

        theory = int(parts[0].strip()) if parts[0].strip().isdigit() else 0
        lab_part = parts[-1].replace(')', '').strip()
        lab = int(lab_part) if lab_part.isdigit() else 0
        return theory, lab
    try:
        return int(hour_string.strip()), 0
    except ValueError:
        return 0, 0


class DataManager:
    def __init__(self):
        self.common_schedule_entries: List[ScheduleEntry] = []
        self.department_courses: Dict[str, Course] = {}
        self.instructors: Dict[str, Instructor] = {}

    def load_common_courses_from_csv(self, file_path: str):
        print(f"-> Ortak dersler yükleniyor: {file_path}")

        if not os.path.exists(file_path):
            print(f"HATA: Dosya bulunamadı -> {file_path}")
            return

        hour_map = {f"{h:02}:20:00": h for h in range(9, 17)}

        with open(file_path, mode='r', encoding='utf-8') as file:
            reader = csv.reader(file)
            header_row = next(reader)
            while 'Monday' not in header_row:
                try:
                    header_row = next(reader)
                except StopIteration:
                    return

            days = header_row[1:6]

            for row in reader:
                if not row or row[0] not in hour_map:
                    continue

                hour = hour_map[row[0]]
                for day_index, day in enumerate(days):
                    cell_content = row[day_index + 1].strip()
                    if not cell_content:
                        continue

                    for course_entry in cell_content.split('\n'):
                        if not course_entry.strip():
                            continue

                        match = re.match(r'(.+?)\s*\((.+?)\)', course_entry.strip())
                        if match:
                            course_code = match.group(1).strip()
                            sections = [int(s.strip()) for s in match.group(2).split(',') if s.strip().isdigit()]

                            for section in sections:
                                temp_course = Course(
                                    code=course_code, name=course_code,
                                    theoretical_hours=1, lab_hours=0, total_hours=1, year=0, capacity=40
                                )
                                entry = ScheduleEntry(
                                    course=temp_course, time_slot=TimeSlot(day=day, hour=hour),
                                    room_id="CommonRoom", section=section, is_lab=False
                                )
                                self.common_schedule_entries.append(entry)
        print(f"-> {len(self.common_schedule_entries)} ortak ders girişi yüklendi.")

    def load_seng_data(self):
        print("-> SENG Müfredat verileri yükleniyor...")
        # ÖRNEK VERİLER (ProBEE belgesinden)
        self._add_course("SENG 101", 1, "4 (3+2)", 70, "S.Esmelioglu", False, 2)
        self._add_course("SENG 201", 2, "4 (3+2)", 60, "I.B.Celikkale", False, 2)
        self._add_course("SENG 303", 3, "3", 40, "S.Esmelioglu", False, 1)
        # ... Diğer dersler buraya eklenecek ...
        print(f"-> {len(self.department_courses)} SENG dersi yüklendi.")

    def _add_course(self, code, year, hours, cap, instructor_id, is_elective, sections):
        theory, lab = parse_hours(hours)
        self.department_courses[code] = Course(
            code=code, name=code, year=year, theoretical_hours=theory, lab_hours=lab,
            total_hours=theory + lab, capacity=cap, instructor_id=instructor_id,
            is_elective=is_elective, section_count=sections
        )