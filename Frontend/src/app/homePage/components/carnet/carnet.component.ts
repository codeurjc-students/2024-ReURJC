import { Component, OnInit } from '@angular/core';
import { ApiAuthService } from 'src/app/services/AuthService/api-auth-service.service';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { Nfc, NfcUtils } from '@capawesome-team/capacitor-nfc';

@Component({
  selector: 'app-carnet',
  templateUrl: './carnet.component.html',
  styleUrls: ['./carnet.component.scss'],
})
export class CarnetComponent implements OnInit {
  private carnetUrl: string | null = null;
  public isNfcActive: boolean = false;
  datos = "Escribir en NFC"
  isNfcSupported: boolean = false;
  isNfcEnabled: boolean = false;
  showMenu = false;
  presentAlert = false;
  public alertButtons = [
    {
      text: 'Cabcelar',
      role: 'cancel',
      handler: () => {
        this.presentAlert = false;
        
      },
    },
    {
      text: 'Ir a ajustes',
      role: 'confirm',
      handler: async () => {
        await Nfc.openSettings();
        this.presentAlert = false;
        
      },
    },
  ];

  

  constructor(
    private userService: ApiUserService,
    private apiAuthService: ApiAuthService
  ) {
    this.checkNfcSupport();
    this.checkNfcEnabled();
  }

  ngOnInit(): void {
    this.apiAuthService.loggedIn$.subscribe((loggedIn) => {
      if (this.getIsLoggedIn()) {
        this.userService.getUserCarnet().subscribe({
          next: (blob) => {
            this.carnetUrl = URL.createObjectURL(blob);
          },
          error: (err) => {
            console.error('Error al obtener el carnet:', err);
          },
        });
      }
    });
  }

  public getIsLoggedIn(): boolean {
    return this.apiAuthService.isLoggedIn();
  }

  public getCarnetUrl(): string | null {
    return this.carnetUrl;
  }

  async checkNfcSupport() {
    const { isSupported } = await Nfc.isSupported();
    this.isNfcSupported = isSupported;
  }

  async checkNfcEnabled() {
    const { isEnabled } = await Nfc.isEnabled();
    this.isNfcEnabled = isEnabled;
  }

  createNdefTextRecord() {
    const utils = new NfcUtils();
    if (this.apiAuthService.isLoggedIn()) {
      const userId = this.apiAuthService.getUser()?.id;
      if (userId !== undefined) {
        this.datos = userId.toString();
      }
    }
    const { record } = utils.createNdefTextRecord({ text: this.datos });
    return record;
  }


  async writeNfcTag() {
    await this.checkNfcEnabled();
    if (this.isNfcSupported && !this.isNfcEnabled) {
      this.presentAlert = true;
      return;
    }
    // Crear el registro NFC
    const record = this.createNdefTextRecord();
    this.showMenu = true

    Nfc.addListener('nfcTagScanned', async () => {
      try {
        await Nfc.write({ message: { records: [record] } });
        await Nfc.stopScanSession();
        alert('Etiqueta escrita con éxito.');
      } catch (error) {
        console.error('Error escribiendo la etiqueta NFC:', error);
        alert('Error escribiendo la etiqueta.');
      }
    });

    // Iniciar la sesión NFC
    Nfc.startScanSession();

    // Terminar la sesión después de 10 segundos
    setTimeout(async () => {
      try {
        await Nfc.stopScanSession();
        this.showMenu = false;
      } catch (error) {
        console.error('Error al terminar la sesión NFC:', error);
      }
    }, 10000); // 10000 milisegundos = 10 segundos
  }
}
