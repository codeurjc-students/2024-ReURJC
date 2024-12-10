import { Component, OnInit } from '@angular/core';
import { ApiAuthService } from 'src/app/services/AuthService/api-auth-service.service';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { Nfc, NfcUtils, NfcTagTechType } from '@capawesome-team/capacitor-nfc';

@Component({
  selector: 'app-carnet',
  templateUrl: './carnet.component.html',
  styleUrls: ['./carnet.component.scss'],
})
export class CarnetComponent implements OnInit {
  private carnetUrl: string | null = null;
  public isNfcActive: boolean = false; // Estado para el indicador NFC
  datos = "Escribir en NFC"

  constructor(
    private userService: ApiUserService, 
    private apiAuthService: ApiAuthService
  ) {}

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

  async writeNfcTag() {
    this.isNfcActive = true; // Activa el indicador NFC
    try {
      const user = this.apiAuthService.getUser();
      if (!user) {
        console.error('No user data found.');
        this.isNfcActive = false;
        return;
      }

      const utils = new NfcUtils();
      const emailRecord = utils.createNdefTextRecord({ text: `Email: ${user.email}` });
      const nameRecord = utils.createNdefTextRecord({ text: `Name: ${user.name}` });

      console.log('Esperando tag NFC...');
      await Nfc.startScanSession();
      this.datos = "sesion empezada"

      Nfc.addListener('nfcTagScanned', async (event) => {
        try {
          console.log('Tag NFC detectado:', event);
          await Nfc.connect({ techType: NfcTagTechType.NfcA });
          await Nfc.write({ message: { records: [emailRecord.record, nameRecord.record] } });
          console.log('Datos escritos exitosamente en el NFC!');
        } catch (writeError) {
          console.error('Error escribiendo en el tag NFC:', writeError);
        } finally {
          await Nfc.stopScanSession();
          this.isNfcActive = false; // Desactiva el indicador NFC
        }
      });
    } catch (error) {
      console.error('Error durante la operación NFC:', error);
      this.isNfcActive = false;
    }
  }
}
