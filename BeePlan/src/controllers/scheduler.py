import os
from typing import List, Dict, Tuple, Optional
from src.models.data_model import Course, TimeSlot, Instructor, ScheduleEntry, DAYS
from src.controllers.data_manager import DataManager  # DataManager'ı kullanacağız


class Scheduler:
    """
    Ders programını oluşturmak için Geri İzleme (Backtracking) algoritmasını uygular.
    """

    def __init__(self, data_manager: DataManager):
        self.dm = data_manager  # Veri Yöneticisine erişim
        self.final_schedule: List[ScheduleEntry] = []
        self.time_slots: List[TimeSlot] = self._generate_all_time_slots()
        self.unassigned_course_slots: List[Tuple[Course, int, bool]] = self._get_course_slots_to_assign()
        # Şu anki programın durumunu tutmak için bir hash tablosu (Hızlı Kontrol için)
        self.current_assignments: Dict[TimeSlot, List[ScheduleEntry]] = {}

    def _generate_all_time_slots(self) -> List[TimeSlot]:
        """Programlama yapılabilecek tüm 50 dakikalık zaman dilimlerini üretir (09:20'den 16:20'ye kadar)."""
        slots = []
        for day in DAYS:
            # 9, 10, ..., 16 saatleri
            for hour in range(9, 17):
                time_slot = TimeSlot(day=day, hour=hour)

                # KRİTİK KISIT 1: Cuma 13:20-15:10 arası sınav bloğu, ders konulamaz.
                # (13:20 = saat 13, 14:20 = saat 14)
                if day == "Friday" and hour in [13, 14]:
                    continue  # Bu slotları atla

                slots.append(time_slot)
        return slots

    def _get_course_slots_to_assign(self) -> List[Tuple[Course, int, bool]]:
        """
        Atanması gereken tüm Teorik ve Lab ders saatlerini tek tek listeler.
        Format: (Course_Objesi, Şube_Numarası, is_lab:bool)
        """
        slots_to_assign = []

        # SENG Zorunlu Dersleri ve Seçmeli Dersleri
        for course in self.dm.department_courses.values():
            for section in range(1, course.section_count + 1):
                # Teorik Saatler
                for _ in range(course.theoretical_hours):
                    slots_to_assign.append((course, section, False))  # False = Teorik

                # Lab Saatleri
                for _ in range(course.lab_hours):
                    slots_to_assign.append((course, section, True))  # True = Lab

        # Önceliklendirme (Programlama verimini artırmak için):
        # En kısıtlı dersleri (örn. Labı olanlar, çok saatli dersler, çok öğrencili dersler) önce atamak iyidir.
        slots_to_assign.sort(key=lambda x: (x[0].lab_hours > 0, x[0].total_hours), reverse=True)

        # Ortak dersler zaten atanmış olarak kabul ediliyor (dm.common_schedule_entries)
        return slots_to_assign

    # ------------------------------------------------------------------
    # ANA ALGORİTMA: BACKTRACKING (GERİ İZLEME)
    # ------------------------------------------------------------------

    def is_valid(self, assignment: ScheduleEntry) -> bool:
        """
        Yeni bir atama (assignment) yapıldığında, programın tüm kısıtları ihlal edip etmediğini kontrol eder.

        Bu fonksiyonda KRİTİK KISITLARIMIZI kontrol edeceğiz:
        1. Derslik Çakışması (Aynı anda aynı yerde iki ders olamaz)
        2. Öğretim Elemanı Çakışması (Aynı anda aynı hoca iki ders veremez)
        3. Öğrenci Sınıf Çakışması (Aynı yılın zorunlu dersleri çakışamaz)
        4. Lab Kuralı (Lab dersi, teorik dersten önce olamaz)
        5. Günlük Hoca Saati Kuralı (Günlük maks. 4 saat teorik)
        """

        # 1. Derslik ve Saat Çakışması Kontrolü (Aynı saatte, aynı yerde başka bir şey var mı?)
        # (Şimdilik derslik atanmadığı için bu kontrolü atlıyoruz, sadece TimeSlot ve Sınıf çakışmasını kontrol edeceğiz)

        # 2. Öğretim Elemanı Çakışması Kontrolü
        # Bu dersi veren hoca, aynı saatte başka bir ders veriyor mu?
        # TODO: self.current_assignments içinde kontrol edilmeli.

        # 3. Lab Kuralı Kontrolü (Lab dersi, teorik dersten önce olmamalıdır)
        if assignment.is_lab:
            # Lab ataması yapılıyorsa, o hafta o şubenin teorik dersi daha önce atanmış olmalı veya
            # Labın teorik slotu hemen önce atanmamış olmalı (en azından aynı gün içinde daha önce atanmış olmalı).
            # TODO: Lab/Teorik kontrolü buraya eklenecek.
            pass

        # 4. Günlük Hoca Saati Kuralı Kontrolü (Günlük maks. 4 saat teorik)
        # TODO: Hocanın o gün kaç teorik dersi var, hesaplanıp 4'ü geçip geçmediği kontrol edilecek.

        # 5. Ortak Dersler ve Diğer Kısıtlar
        # Ortak derslerin saatleri dolu olduğu için, yeni atama bu dolu slotlara denk gelemez.
        # TODO: dm.common_schedule_entries listesi kontrol edilmeli.

        # Tüm kontroller geçerliyse True döndür
        return True

    def solve(self, slot_index=0) -> Optional[List[ScheduleEntry]]:
        """
        Backtracking (Geri İzleme) algoritması ile programı çözmeye çalışır.
        """
        # Bitiş Durumu: Tüm dersler başarılı bir şekilde atanmışsa, çözümü döndür.
        if slot_index == len(self.unassigned_course_slots):
            return self.final_schedule  # Başarılı Çözüm!

        current_slot_info = self.unassigned_course_slots[slot_index]
        course, section, is_lab = current_slot_info

        # Tüm olası zaman dilimlerini dene
        for time_slot in self.time_slots:

            # Yeni bir deneme ataması oluştur
            # (Oda bilgisini şimdilik "TempRoom" olarak varsayıyoruz)
            new_assignment = ScheduleEntry(
                course=course, time_slot=time_slot, room_id="TempRoom",
                section=section, is_lab=is_lab
            )

            # Kontrol et: Bu atama tüm kısıtları sağlıyor mu?
            if self.is_valid(new_assignment):

                # Atama geçerliyse, bu atamayı yap (programı güncelle)
                self.final_schedule.append(new_assignment)
                # self.current_assignments[time_slot].append(new_assignment) # Hızlı kontrol yapısı

                # Bir sonraki dersi atamak için özyinelemeli (recursive) çağrı yap
                result = self.solve(slot_index + 1)

                if result is not None:
                    return result  # Çözüm bulundu, yukarı doğru döndür

                # Çözüm bulunamadıysa: Geri İzle (Backtrack)
                self.final_schedule.pop()  # Yanlış atamayı geri al
                # self.current_assignments[time_slot].pop() # Hızlı kontrol yapısını geri al

        # Tüm zaman dilimleri denendi ve çözüm bulunamadı
        return None