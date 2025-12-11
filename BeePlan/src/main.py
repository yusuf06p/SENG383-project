import os
import sys
from src.controllers.data_manager import DataManager
from src.controllers.scheduler import Scheduler
from src.views.gui_main import run_gui  # Yeni import


def main():
    print("=== BeePlan Python Başlatılıyor ===")

    manager = DataManager()

    # --- 1. Verileri Yükle ---
    csv_path = os.path.join("data", "Muhendislik_Fakultesi.csv")
    # Not: Dosya adının kısaltılmış (Muhendislik_Fakultesi.csv) olduğunu varsayıyorum.
    manager.load_common_courses_from_csv(csv_path)
    manager.load_seng_data()

    # --- 2. Program Çözücüyü Başlat ---
    scheduler = Scheduler(manager)
    print(f"\n-> Atanacak Toplam Ders/Lab Saati: {len(scheduler.unassigned_course_slots)}")

    print("\n=== Ders Programı Çözülüyor... (Bu biraz zaman alabilir) ===")

    final_schedule = scheduler.solve()

    # --- 3. GUI'yi Başlat ---
    if final_schedule:
        print("\n✅ Çözüm Başarılı! Arayüz Başlatılıyor...")

        # Çözülen programı GUI fonksiyonuna gönder
        run_gui(final_schedule)
    else:
        print("\n❌ HATA: Ders programı için çakışmasız bir çözüm bulunamadı. Lütfen kısıtları gözden geçirin.")


if __name__ == "__main__":
    # Windows'da PyQt6'nın düzgün çalışması için gerekebilir
    if sys.platform == 'win32':
        import ctypes

        app_id = 'BeePlan.Scheduler.1.0'
        ctypes.windll.shell32.SetCurrentProcessExplicitAppUserModelID(app_id)

    main()