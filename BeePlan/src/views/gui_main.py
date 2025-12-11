import sys
from PyQt6.QtWidgets import (
    QApplication, QMainWindow, QWidget, QVBoxLayout, QHBoxLayout,
    QPushButton, QTableWidget, QTableWidgetItem, QHeaderView, QLabel,
    QStackedWidget, QSizePolicy
)
from PyQt6.QtCore import Qt
from PyQt6.QtGui import QColor, QFont
from typing import List
from src.controllers.data_manager import DataManager
from src.controllers.scheduler import Scheduler
from src.models.data_model import ScheduleEntry, DAYS

# --- CSS (QSS) ile Arayüz Stilini Belirleme ---
# Tasarıma daha yakın modern bir görünüm için temel bir QSS stili
GUI_STYLESHEET = """
QMainWindow {
    background-color: #f0f0f0;
}
#Sidebar {
    background-color: #2c3e50; /* Koyu Mavi */
    border-right: 1px solid #34495e;
}
.SidebarButton {
    text-align: left;
    padding: 10px 15px;
    color: #ecf0f1;
    border: none;
    border-radius: 0;
    font-size: 14px;
}
.SidebarButton:hover {
    background-color: #34495e;
}
.SidebarButton:checked {
    background-color: #1abc9c; /* Aktif Buton Rengi */
    font-weight: bold;
}
QTableWidget {
    gridline-color: #bdc3c7;
    font-size: 12px;
    border: 1px solid #bdc3c7;
}
QTableWidget::item {
    padding: 5px;
}
QHeaderView::section {
    background-color: #3498db; /* Mavi Başlık */
    color: white;
    font-weight: bold;
    padding: 6px;
    border: 1px solid #2980b9;
}
"""


class TimetableWidget(QTableWidget):
    """Programın görüntülendiği takvim ızgarası."""

    def __init__(self, schedule_entries: List[ScheduleEntry]):
        super().__init__()

        self.setRowCount(8)  # 09:20'den 16:20'ye kadar 8 saat
        self.setColumnCount(5)  # Pazartesi'den Cuma'ya kadar 5 gün
        self.entries = schedule_entries

        self.setHorizontalHeaderLabels(DAYS)
        self.setVerticalHeaderLabels([f"{h}:20" for h in range(9, 17)])

        self.horizontalHeader().setSectionResizeMode(QHeaderView.ResizeMode.Stretch)
        self.verticalHeader().setSectionResizeMode(QHeaderView.ResizeMode.Stretch)

        self.setEditTriggers(QTableWidget.EditTrigger.NoEditTriggers)  # Düzenlemeyi kapat

        self._populate_timetable()

    def _populate_timetable(self):
        """Hesaplanan ders programını ızgaraya yerleştirir."""

        # Programı TimeSlot ve Oda/Şube'ye göre grupla
        schedule_map = {}
        for entry in self.entries:
            key = (entry.time_slot.day, entry.time_slot.hour)
            if key not in schedule_map:
                schedule_map[key] = []
            schedule_map[key].append(entry)

        hour_to_row = {h: i for i, h in enumerate(range(9, 17))}

        for i, day in enumerate(DAYS):
            for hour in range(9, 17):
                row = hour_to_row.get(hour)

                # Cuma 13:20 ve 14:20 sınav bloğu
                if day == "Friday" and hour in [13, 14]:
                    item = QTableWidgetItem("Sınav Bloğu")
                    item.setForeground(QColor(Qt.GlobalColor.white))
                    self.setItem(row, i, item)
                    self.item(row, i).setBackground(QColor(Qt.GlobalColor.darkRed))
                    continue

                entries_list = schedule_map.get((day, hour), [])

                if entries_list:
                    # Hücre içeriğini oluştur
                    content = ""
                    for entry in entries_list:
                        content += f"{entry.course.code} (Ş{entry.section}) - {'LAB' if entry.is_lab else 'T'}\n"

                    item = QTableWidgetItem(content.strip())

                    # Hücre stillerini ayarla
                    item.setFont(QFont("Arial", 9))
                    # Ortak dersleri sarı, SENG derslerini mavi yapabiliriz
                    if any(e.course.year == 0 for e in entries_list):  # Ortak ders
                        item.setBackground(QColor("#f1c40f"))  # Sarı
                    else:  # Bölüm dersi
                        item.setBackground(QColor("#a9cce3"))  # Açık Mavi

                    self.setItem(row, i, item)
                else:
                    # Boş slot
                    self.setItem(row, i, QTableWidgetItem(""))


class MainWindow(QMainWindow):
    def __init__(self, final_schedule: List[ScheduleEntry]):
        super().__init__()
        self.setWindowTitle("BeePlan - Ders Programı Çözücü (Python/PyQt6)")
        self.setGeometry(100, 100, 1200, 700)
        self.setStyleSheet(GUI_STYLESHEET)

        self.final_schedule = final_schedule
        self._setup_ui()

    def _setup_ui(self):
        main_widget = QWidget()
        main_layout = QHBoxLayout(main_widget)
        main_layout.setContentsMargins(0, 0, 0, 0)

        # --- Sol Kenar Çubuğu (Sidebar) ---
        sidebar = QWidget()
        sidebar.setObjectName("Sidebar")
        sidebar.setFixedWidth(200)
        sidebar_layout = QVBoxLayout(sidebar)
        sidebar_layout.setContentsMargins(0, 0, 0, 10)
        sidebar_layout.setSpacing(0)

        logo = QLabel("🐝 BeePlan")
        logo.setFont(QFont("Arial", 18, QFont.Weight.Bold))
        logo.setStyleSheet("color: white; padding: 20px 15px; background-color: #16a085;")
        sidebar_layout.addWidget(logo)

        # Butonlar (Tasarımınızdaki Menü Elemanları)

        btn_dashboard = QPushButton("🏠 Dashboard")
        btn_data = QPushButton("📊 Veri Yönetimi")
        btn_constraints = QPushButton("⚙️ Kısıt Ayarları")
        btn_generate = QPushButton("🔄 Program Oluştur")
        btn_view = QPushButton("📅 Programı Görüntüle")

        for btn in [btn_dashboard, btn_data, btn_constraints, btn_generate, btn_view]:
            btn.setObjectName("SidebarButton")
            btn.setCheckable(True)  # Aktif butonu göstermek için
            sidebar_layout.addWidget(btn)

        sidebar_layout.addStretch()  # Butonları yukarıya yasla

        main_layout.addWidget(sidebar)

        # --- Sağ Ana İçerik Alanı (Stacked Widget) ---

        self.stacked_widget = QStackedWidget()

        # 1. Dashboard Sayfası
        dashboard_page = QLabel("Proje Bilgileri ve Özeti (Dashboard)")
        dashboard_page.setAlignment(Qt.AlignmentFlag.AlignCenter)
        dashboard_page.setFont(QFont("Arial", 20))

        # 2. Programı Görüntüle Sayfası (Kritik)
        if self.final_schedule:
            timetable_page = TimetableWidget(self.final_schedule)
        else:
            timetable_page = QLabel("Henüz bir çözüm bulunamadı veya yüklenmedi.")
            timetable_page.setAlignment(Qt.AlignmentFlag.AlignCenter)

        # Sayfaları Stacked Widget'a ekle
        self.stacked_widget.addWidget(dashboard_page)  # Index 0
        self.stacked_widget.addWidget(timetable_page)  # Index 1

        main_layout.addWidget(self.stacked_widget)

        self.setCentralWidget(main_widget)

        # Buton Bağlantıları
        btn_view.clicked.connect(lambda: self.stacked_widget.setCurrentIndex(1))
        btn_dashboard.clicked.connect(lambda: self.stacked_widget.setCurrentIndex(0))
        btn_view.setChecked(True)  # Başlangıçta programı göster


# -----------------------------------------------------------
# ANA ÇALIŞTIRMA MANTIĞI (main.py'den çağrılacak)
# -----------------------------------------------------------

def run_gui(final_schedule: List[ScheduleEntry]):
    """Scheduler tarafından çözülen programı alıp GUI'yi başlatır."""
    app = QApplication(sys.argv)
    window = MainWindow(final_schedule)
    window.show()
    sys.exit(app.exec())