import { Component } from '@angular/core';
import { Subscription } from 'rxjs';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { CardInfo } from '../components/Card/card/CardInfo';

@Component({
  selector: 'app-postulate-as-delegate',
  templateUrl: './postulate-as-delegate.component.html',
  styleUrls: ['./postulate-as-delegate.component.scss'],
})
export class PostulateAsDelegateComponent {
  private subscription: Subscription = new Subscription(); // Para gestionar la suscripción
  private isCandidate: boolean = false;
  cardInfo: CardInfo = new CardInfo(
    "Inscríbete",
    "Marca la diferencia.",
    "Al inscribirte entras en el listado donde tus compañeros podrán votarte.",
    "" // No necesitas apiCaller en este caso
  );

  constructor(private apiUserService: ApiUserService) { }

  // Método para comprobar si el usuario es candidato
  getIsCandidate(): boolean {
    return this.isCandidate;
  }

  // Método para convertirse en candidato o cancelar la candidatura
  becomeCandidate() {
    if (this.getIsCandidate()) {
      // Si ya es candidato, cancelamos la candidatura
      this.cancelCandidacy();
    } else {
      // Si no es candidato, lo convertimos en candidato
      this.subscribeAsCandidate();
    }
  }

  // Método para suscribirse como candidato
  subscribeAsCandidate() {
    this.subscription.add(
      this.apiUserService.becomeCandidate().subscribe(
        (response) => {
          this.isCandidate = true; // Actualizamos el estado a candidato
        },
        (error) => {
          console.error('Error al postularse como candidato', error);
        }
      )
    );
  }


  cancelCandidacy() {
    this.subscription.add(
      this.apiUserService.cancelCandidacy().subscribe(
        (response) => {
          this.isCandidate = false; // Actualizamos el estado a no candidato
        },
        (error) => {
          console.error('Error al cancelar la candidatura', error);
        }
      )
    );
  }

}
