import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEtudiant } from '../etudiant.model';
import { EtudiantService } from '../service/etudiant.service';

@Component({
  templateUrl: './etudiant-delete-dialog.component.html',
})
export class EtudiantDeleteDialogComponent {
  etudiant?: IEtudiant;

  constructor(protected etudiantService: EtudiantService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.etudiantService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
