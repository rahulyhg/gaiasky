package gaia.cu9.ari.gaiaorbit.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import gaia.cu9.ari.gaiaorbit.scenegraph.SceneGraphNode;
import gaia.cu9.ari.gaiaorbit.util.PrefixedProperties;

import java.util.*;

/**
 * Manages celestial body providers and gets their data.
 * @author Toni Sagrista
 *
 */
public class SceneGraphNodeProviderManager {

    private List<ISceneGraphNodeProvider> providers;
    private Set<Class<? extends ISceneGraphNodeProvider>> clazzes;

    public SceneGraphNodeProviderManager() {
        providers = new LinkedList<ISceneGraphNodeProvider>();
        clazzes = new HashSet<Class<? extends ISceneGraphNodeProvider>>();
    }

    public final void addProviders(Properties prop, String... providerClassNames) {
        Class<? extends ISceneGraphNodeProvider>[] classes = new Class[providerClassNames.length];
        int i = 0;
        try {
            for (String className : providerClassNames) {
                Class clazz = Class.forName(className);
                if (!ISceneGraphNodeProvider.class.isAssignableFrom(clazz)) {
                    Gdx.app.error("SceneGraphNodeProviderManager", "Class " + clazz + " not instance of " + ISceneGraphNodeProvider.class.getCanonicalName());
                } else {
                    classes[i] = clazz;
                    i++;
                }

            }
        } catch (Exception e) {
            Gdx.app.error("SceneGraphNodeProviderManager", "Error loading provider classes", e);
        }
        addProviders(prop, classes);
    }

    @SafeVarargs
    public final void addProviders(Properties prop, Class<? extends ISceneGraphNodeProvider>... classes) {

        for (Class<? extends ISceneGraphNodeProvider> clazz : classes) {
            // No repeated providers
            if (!clazzes.contains(clazz)) {
                try {
                    ISceneGraphNodeProvider provider = clazz.newInstance();

                    PrefixedProperties pp = new PrefixedProperties(prop, clazz.getName() + ".");

                    provider.initialize(pp);
                    providers.add(provider);
                    clazzes.add(clazz);
                } catch (InstantiationException | IllegalAccessException e) {
                    Gdx.app.log(SceneGraphNodeProviderManager.class.getSimpleName(), e.getLocalizedMessage());
                }
            }

        }
    }

    public List<SceneGraphNode> loadObjects() {
        List<SceneGraphNode> l = new ArrayList<SceneGraphNode>(120000);
        for (ISceneGraphNodeProvider provider : providers) {
            l.addAll(provider.loadObjects());
        }
        return l;
    }

    public List<SceneGraphNode> loadObjects(Class<? extends ISceneGraphNodeProvider> providerClass, AssetManager am) {
        List<SceneGraphNode> l = new ArrayList<SceneGraphNode>();
        if (clazzes.contains(providerClass)) {
            for (ISceneGraphNodeProvider provider : providers) {
                if (provider.getClass().equals(providerClass)) {
                    l.addAll(provider.loadObjects());
                    return l;
                }
            }
        }
        return l;
    }

}
