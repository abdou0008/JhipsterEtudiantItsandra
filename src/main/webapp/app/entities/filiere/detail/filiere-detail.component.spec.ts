import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FiliereDetailComponent } from './filiere-detail.component';

describe('Filiere Management Detail Component', () => {
  let comp: FiliereDetailComponent;
  let fixture: ComponentFixture<FiliereDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FiliereDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ filiere: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FiliereDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FiliereDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load filiere on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.filiere).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
