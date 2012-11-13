
package it.wm.android.adaptor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

import it.wm.perdue.EsercentiListFragment;

import java.util.ArrayList;
import java.util.List;

public class EsercentiPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    
    protected String[]     CONTENT      = new String[] {
            "Distanza", "Nome"
                                        };
    
    private int            mCount       = CONTENT.length;
    private String         category     = null;
    private List<Fragment> fragmentList = null;
    
    public EsercentiPagerAdapter(FragmentManager fm, String category) {
        super(fm);
        this.category = category;
        fragmentList = new ArrayList<Fragment>(mCount);
        for (int i = 0; i < mCount; i++) {
            fragmentList.add(i, null);
        }
    }
    
    @Override
    public Fragment getItem(int position) {
        
        // il problema ora  che bisogna gestire con il fragment manager i vari
        // fragment?
        // inoltre bisognerebbe ritornare ad "esercentiBaseActivity" quale
        // fragment  visualizzato,
        // perch quando si fa ad esempio la ricerca o si cambia filtro, bisogna
        // inviare al fragment i dati per le nuove query
        
        // Mario, vanno i dati per le nuove query vanno inviati a TUTTI i
        // fragment, perchŽ quando fai lo swipe le views devono apparire giˆ
        // aggiornate
        
        // TODO: dirty, bisognrebbe usare il Fragment Manager
        Fragment f = fragmentList.get(position);
        if (f == null) {
            f = EsercentiListFragment
                    .newInstance(CONTENT[position % CONTENT.length], category);
            fragmentList.add(position, f);
        }
        return f;
        
        // return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }
    
    @Override
    public int getCount() {
        return mCount;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length];
    }
    
    // public void setCount(int count) {
    // if (count > 0 && count <= 2) {
    // mCount = count;
    // notifyDataSetChanged();
    // }
    // }
    
    @Override
    public int getIconResId(int index) {
        // TODO Auto-generated method stub
        return 0;
    }
}
